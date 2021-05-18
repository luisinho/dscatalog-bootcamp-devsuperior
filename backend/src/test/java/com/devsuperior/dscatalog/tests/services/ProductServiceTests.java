package com.devsuperior.dscatalog.tests.services;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.factory.ProductFactory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService productService;

	@Mock
	private ProductRepository productRepository;

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private Product product;
	private PageImpl<Product> page;
	private PageRequest pageRequest;
	private long totalPageRequest;
	private ProductDTO productDTO;
	private Product productUpdate;

	@BeforeEach
	void setUp() throws Exception {
		this.existingId = 1;
		this.nonExistingId = 10000;
		this.dependentId = 4;
		this.product = ProductFactory.createProduct();
		this.page = new PageImpl<>(Arrays.asList(this.product));
		this.pageRequest = PageRequest.of(1, 30);
		this.totalPageRequest = 1;
		this.productDTO = ProductFactory.createProductDTO();

		Mockito.when(this.productRepository.getOne(this.nonExistingId))
		.thenReturn(this.productUpdate);

		Mockito.when(this.productRepository.getOne(this.existingId))
		.thenReturn(this.product);

		Mockito.when(this.productRepository.find(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any()))
		.thenReturn(this.page);

		Mockito.when(this.productRepository.save(ArgumentMatchers.any())).thenReturn(this.product);

		Mockito.when(this.productRepository.findById(this.existingId)).thenReturn(Optional.of(this.product));
		Mockito.when(this.productRepository.findById(this.nonExistingId)).thenReturn(Optional.empty());
		
		Mockito.doNothing().when(this.productRepository).deleteById(this.existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(this.productRepository).deleteById(this.nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(this.productRepository).deleteById(this.dependentId);
	}

	@Test
	public void deleteShouldThrowDataBaseExceptionWhenDependentId() {

		Assertions.assertThrows(DataBaseException.class, () -> {
			this.productService.delete(this.dependentId);
		});

		Mockito.verify(this.productRepository, Mockito.times(1)).deleteById(this.dependentId);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {		

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			this.productService.delete(this.nonExistingId);
		});

		Mockito.verify(this.productRepository, Mockito.times(1)).deleteById(this.nonExistingId);
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {		

		Assertions.assertDoesNotThrow(() -> {
			this.productService.delete(this.existingId);
		});

		Mockito.verify(this.productRepository, Mockito.times(1)).deleteById(this.existingId);
	}

	@Test
	public void findAllPagedShouldReturnOnePageAndCallMethodFindRepository() {

		Page<ProductDTO> result = this.productService.findAllPaged(0L, "", this.pageRequest);

		Assertions.assertNotNull(result);

		Assertions.assertTrue(result.getTotalPages() == this.totalPageRequest);

		Mockito.verify(this.productRepository, Mockito.times(1)).find(null, "", this.pageRequest);
	}

	@Test
	public void findByIdShouldReturnProductWhenIdExists() {

		ProductDTO result = this.productService.findById(this.existingId);

		Assertions.assertNotNull(result);

		Mockito.verify(this.productRepository, Mockito.times(1)).findById(this.existingId);
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			this.productService.findById(this.nonExistingId);
		});

		Mockito.verify(this.productRepository, Mockito.times(1)).findById(this.nonExistingId);
	}

	@Test
	public void updateShouldReturnProductWhenIdExists() {

		ProductDTO result = this.productService.update(this.existingId, this.productDTO);

		Assertions.assertTrue(result.getId() == this.existingId);

		Mockito.verify(this.productRepository, Mockito.times(1)).save(this.product);
	}

	/*@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			this.productService.update(this.nonExistingId, this.productDTO);
		});

		Mockito.verify(this.productRepository, Mockito.times(1)).save(this.productUpdate);
	}*/
}