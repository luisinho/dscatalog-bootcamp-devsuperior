package com.devsuperior.dscatalog.tests.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.factory.ProductFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceCorrectionTests {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductService productService;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${security.oauth2.client.client-id}")
	private String clientId;

	@Value("${security.oauth2.client.client-secret}")	
	private String clientSecret;
	
	private ProductDTO newProductDTO;
	private ProductDTO existingProductDTO;

	private long existingId;
	private long nonExistingId;
	private String operatorUserName;
	private String operatorPassword;

	@BeforeEach
	void setUp() throws Exception {

		this.existingId = 1L;

		this.nonExistingId = 2L;

		this.operatorUserName = "alex@gmail.com";

		this.operatorPassword = "123456";

		this.newProductDTO = ProductFactory.createProductDTO(null);
		
		this.existingProductDTO = ProductFactory.createProductDTO(this.existingId);

		when(this.productService.insert(any())).thenReturn(this.existingProductDTO);

		doThrow(ResourceNotFoundException.class).when(this.productService).delete(this.nonExistingId);
	}

	@Test
	public void insertShouldReturnCreatedProductWhenValidData() throws Exception {

		String accessToken = this.obtainAccessToken(this.operatorUserName, this.operatorPassword);

		String jsonBody = this.objectMapper.writeValueAsString(this.newProductDTO);

		ResultActions result =
				   this.mockMvc.perform(post("/products")
					   .header("Authorization", "Bearer " + accessToken)
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
	}

	@Test
	public void insertShouldReturnUnProcessableEntityWhenNegativePrice() throws Exception {

		String accessToken = this.obtainAccessToken(this.operatorUserName, this.operatorPassword);

		this.newProductDTO.setPrice(-1.0);
		String jsonBody = this.objectMapper.writeValueAsString(this.newProductDTO);

		ResultActions result =
				   this.mockMvc.perform(post("/products")
					   .header("Authorization", "Bearer " + accessToken)
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {

		String accessToken = this.obtainAccessToken(this.operatorUserName, this.operatorPassword);

		ResultActions result =
				   this.mockMvc.perform(delete("/products/{id}", this.existingId)
					   .header("Authorization", "Bearer " + accessToken)					   
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNoFoundWhenIdDoesNotExist() throws Exception {

		String accessToken = this.obtainAccessToken(this.operatorUserName, this.operatorPassword);

		ResultActions result =
				   this.mockMvc.perform(delete("/products/{id}", this.nonExistingId)
					   .header("Authorization", "Bearer " + accessToken)					   
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	private String obtainAccessToken(String username, String password) throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

		params.add("grant_type", "password");
		params.add("client_id", this.clientId);
		params.add("username", username);
		params.add("password", password);

		ResultActions result
		    = this.mockMvc.perform(post("/oauth/token")
		    		.params(params)
		    		.with(httpBasic(this.clientId, this.clientSecret))
		    		.accept("application/json;charset=UTF-8"))
		            .andExpect(status().isOk())
		            .andExpect(content().contentType("application/json;charset=UTF-8"));

		String resultString = result.andReturn().getResponse().getContentAsString();

		JacksonJsonParser jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(resultString).get("access_token").toString();

	}

}
