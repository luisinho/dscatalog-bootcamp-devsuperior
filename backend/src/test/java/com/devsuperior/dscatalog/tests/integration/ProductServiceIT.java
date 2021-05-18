package com.devsuperior.dscatalog.tests.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIT {

	@Autowired
	private ProductService productService;

	private long existingId;
	private long nonExistingId;
	private long countTotalProduct;
	private long countPCGamerProducts;
	private PageRequest pageRequest;

	@BeforeEach
	void setUp() throws Exception {
		this.existingId = 1;
		this.nonExistingId = 10000;
		this.countTotalProduct = 25;
		this.countPCGamerProducts = 21;
		this.pageRequest = PageRequest.of(0, 10);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			this.productService.delete(this.nonExistingId);
		});
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {
			this.productService.delete(this.existingId);
		});
	}

	@Test
	public void findAllPagedShouldNotReturnNothingWhenNameDoesNotExist() {

		String name = "Camera";

		Page<ProductDTO> result = this.productService.findAllPaged(0L, name, this.pageRequest);

		Assertions.assertTrue(result.isEmpty());
	}

	@Test
	public void findAllPagedShouldReturnAllProductsWhenNameIsEmpty() {

		String name = "";

		Page<ProductDTO> result = this.productService.findAllPaged(0L, name, this.pageRequest);

		Assertions.assertFalse(result.isEmpty());

		Assertions.assertEquals(this.countTotalProduct, result.getTotalElements());
	}

	@Test
	public void findAllPagedShouldReturnProductsWhenNameExistsIgnoringCase() {

		String name = "pc gAMeR";

		Page<ProductDTO> result = this.productService.findAllPaged(0L, name, this.pageRequest);

		Assertions.assertFalse(result.isEmpty());

		Assertions.assertEquals(this.countPCGamerProducts, result.getTotalElements());
	}

}