package com.devsuperior.dscatalog.tests.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.factory.ProductFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceTests {

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

	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private ProductDTO newProductDTO;
	private ProductDTO existingProductDTO;
	private PageImpl<ProductDTO> page;

	private String adminUserName;
	private String operatorUserName;
	private String operatorPassword;

	@BeforeEach
	void setUp() throws Exception {

		this.adminUserName = "maria@gmail.com";

		this.operatorUserName = "alex@gmail.com";

		this.operatorPassword = "123456";

		this.existingId = 1L;

		this.nonExistingId = 2L;
		
		this.dependentId = 3L;

		this.newProductDTO = ProductFactory.createProductDTO(null);

		this.existingProductDTO = ProductFactory.createProductDTO(this.existingId);

		this.page = new PageImpl<>(Arrays.asList(this.existingProductDTO));

		when(this.productService.findById(this.existingId)).thenReturn(this.existingProductDTO);

		when(this.productService.findById(this.nonExistingId)).thenThrow(ResourceNotFoundException.class);

		when(this.productService.findAllPaged(any(), anyString(), any())).thenReturn(this.page);

		when(this.productService.insert(any())).thenReturn(this.existingProductDTO);

		when(this.productService.update(eq(this.existingId), any())).thenReturn(this.existingProductDTO);

		when(this.productService.update(eq(this.nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

		doNothing().when(this.productService).delete(this.existingId);

		doThrow(ResourceNotFoundException.class).when(this.productService).delete(this.nonExistingId);

		doThrow(DataBaseException.class).when(this.productService).delete(this.dependentId);
	}

	@Test
	public void deleteShouldReturnNoContentWhenIdExistis() throws Exception {

		String accessToken = this.obtainAccessToken(this.adminUserName, this.operatorPassword);

		ResultActions result =
				   this.mockMvc.perform(delete("/products/{id}", this.existingId)
					   .header("Authorization", "Bearer " + accessToken)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNoContent());
	}

	@Test
	public void deleteShouldReturnNoFoundWhenIdDoesNotExist() throws Exception {

		String accessToken = this.obtainAccessToken(this.adminUserName, this.operatorPassword);

		ResultActions result =
				   this.mockMvc.perform(delete("/products/{id}", this.nonExistingId)
					   .header("Authorization", "Bearer " + accessToken)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void insertShouldReturnUnProcessableEntityWhenDoesNotValidPrice() throws Exception {

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
	public void insertShouldReturnProductDTOWhenCreated() throws Exception {

		String accessToken = this.obtainAccessToken(this.operatorUserName, this.operatorPassword);

		String jsonBody = this.objectMapper.writeValueAsString(this.newProductDTO);

		ResultActions result =
				   this.mockMvc.perform(post("/products")
					   .header("Authorization", "Bearer " + accessToken)
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
	}

	@Test
	public void updateShouldReturnProductDTOWhenIdExistis() throws Exception {

		String accessToken = this.obtainAccessToken(this.operatorUserName, this.operatorPassword);

		String jsonBody = this.objectMapper.writeValueAsString(this.newProductDTO);

		String expectedName = this.newProductDTO.getName();
		
		Double expectedPrice = this.newProductDTO.getPrice();

		ResultActions result =
				   this.mockMvc.perform(put("/products/{id}", this.existingId)
					   .header("Authorization", "Bearer " + accessToken)
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.id").value(this.existingId));
		result.andExpect(jsonPath("$.name").value(expectedName));
		result.andExpect(jsonPath("$.price").value(expectedPrice));
	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

		String accessToken = this.obtainAccessToken(this.operatorUserName, this.operatorPassword);

		String jsonBody = this.objectMapper.writeValueAsString(this.newProductDTO);

		ResultActions result =
				   this.mockMvc.perform(put("/products/{id}", this.nonExistingId)
					   .header("Authorization", "Bearer " + accessToken)
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void findAllShouldReturnPage() throws Exception {

		ResultActions result =
		   this.mockMvc.perform(get("/products")
			   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content").exists());
	}

	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception {

		ResultActions result =
		   this.mockMvc.perform(get("/products/{id}", this.existingId)
			   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.id").value(this.existingId));
	}

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception{

		ResultActions result =
				   this.mockMvc.perform(get("/products/{id}", this.nonExistingId)
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
