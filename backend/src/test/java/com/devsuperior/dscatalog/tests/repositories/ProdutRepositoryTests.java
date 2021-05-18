package com.devsuperior.dscatalog.tests.repositories;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.tests.factory.ProductFactory;

@DataJpaTest
public class ProdutRepositoryTests {

	@Autowired
	private ProductRepository productRepository;

	private long existingId;
	private long nonExistingId;
	private long countTotalProduct;
	private long countPCGamerProducts;
	private Category electronicsCategory;
	private Category nonExistingCategory;
	private long countProductsElectronics;
	private Pageable pageable;

	@BeforeEach
	void setUp() throws Exception {
		this.existingId = 1;
		this.nonExistingId = 1000;
		this.countTotalProduct = 25;
		this.countPCGamerProducts = 21;
		this.electronicsCategory = new Category(2L, null);
		this.nonExistingCategory = new Category(1000L, null);
		this.countProductsElectronics = 2;		
		this.pageable = PageRequest.of(0, 10);
	}

	@Test
	public void findShouldNotReturnProductsWhenCategoryNotExists() {

		String name = "";

		Page<Product> result = this.productRepository.find(Arrays.asList(this.nonExistingCategory), name, this.pageable);

		Assertions.assertTrue(result.isEmpty());

		Assertions.assertEquals(0, result.getTotalElements());
	}

	@Test
	public void findShouldReturnProductsWhenElectronicsCategory() {

		String name = "";

		Page<Product> result = this.productRepository.find(Arrays.asList(this.electronicsCategory), name, this.pageable);

		Assertions.assertFalse(result.isEmpty());

		Assertions.assertEquals(this.countProductsElectronics, result.getTotalElements());
	}

	@Test
	public void findShouldReturnAllProductsWhenNameIsEmpty() {

		String name = "";

		Page<Product> result = this.productRepository.find(null, name, this.pageable);

		Assertions.assertFalse(result.isEmpty());

		Assertions.assertEquals(this.countTotalProduct, result.getTotalElements());
	}

	@Test
	public void findShouldReturnProductsWhenNameExistsIgnoringCase() {

		String name = "pc gAMeR";

		Page<Product> result = this.productRepository.find(null, name, this.pageable);

		Assertions.assertFalse(result.isEmpty());

		Assertions.assertEquals(this.countPCGamerProducts, result.getTotalElements());
	}

	@Test
	public void findShouldReturnProductsWhenNameExists() {

		String name = "PC Gamer";

		Page<Product> result = this.productRepository.find(null, name, this.pageable);

		Assertions.assertFalse(result.isEmpty());

		Assertions.assertEquals(this.countPCGamerProducts, result.getTotalElements());
	}

	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {

		Product product = ProductFactory.createProduct();
		product.setId(null);

		product = this.productRepository.save(product);

		Optional<Product> result = this.productRepository.findById(product.getId());

		Assertions.assertNotNull(product.getId());

		Assertions.assertEquals(this.countTotalProduct + 1, product.getId());

		Assertions.assertTrue(result.isPresent());

		Assertions.assertSame(result.get(), product);
	}

	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {

		this.productRepository.deleteById(this.existingId);

		Optional<Product> result = this.productRepository.findById(this.existingId);

		Assertions.assertFalse(result.isPresent());		
	}

	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			
			this.productRepository.deleteById(this.nonExistingId);
		});
	}
}
