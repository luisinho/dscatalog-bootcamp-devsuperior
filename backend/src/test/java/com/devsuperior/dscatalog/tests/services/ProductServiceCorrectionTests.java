package com.devsuperior.dscatalog.tests.services;

import java.util.Arrays;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.factory.ProductFactory;

@ExtendWith(SpringExtension.class)
public class ProductServiceCorrectionTests {

	@InjectMocks
	private ProductService productService;

	@Mock
	private ProductRepository productRepository;

	private long existingId;
	private long nonExistingId;
	private Product product;
	private PageImpl<Product> page;

	@BeforeEach
	void setUp() throws Exception {

		this.existingId = 1;

		this.nonExistingId = 10000;

		this.product = ProductFactory.createProduct();

		this.page = new PageImpl<>(Arrays.asList(this.product));

		Mockito.when(this.productRepository.getOne(this.existingId))
		.thenReturn(this.product);

		Mockito.doThrow(EntityNotFoundException.class).when(this.productRepository).getOne(this.nonExistingId);

		Mockito.when(this.productRepository.findById(this.existingId)).thenReturn(Optional.of(this.product));

		Mockito.when(this.productRepository.find(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any()))
		.thenReturn(this.page);
		
		Mockito.when(this.productRepository.save(ArgumentMatchers.any())).thenReturn(this.product);
	}

	@Test
	public void findAllPagedShouldReturnPage() {

		Long categoryId = 0L;
		String name = "";
		PageRequest pageRequest = PageRequest.of(0, 10);

		Page<ProductDTO> result = this.productService.findAllPaged(categoryId, name, pageRequest);

		Assertions.assertNotNull(result);

		Assertions.assertFalse(result.isEmpty());

		Mockito.verify(this.productRepository, Mockito.times(1)).find(null, "", pageRequest);
	}

	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {

		ProductDTO result = this.productService.findById(this.existingId);

		Assertions.assertNotNull(result);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			this.productService.findById(this.nonExistingId);			
		});		
	}

	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {

		ProductDTO dto = new ProductDTO();

		ProductDTO result = this.productService.update(this.existingId, dto);

		Assertions.assertNotNull(result);
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

		ProductDTO dto = new ProductDTO();

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			this.productService.update(this.nonExistingId, dto);
		});
	}
}