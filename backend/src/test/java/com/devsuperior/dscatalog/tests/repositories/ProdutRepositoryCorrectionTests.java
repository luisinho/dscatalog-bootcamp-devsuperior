package com.devsuperior.dscatalog.tests.repositories;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;

@DataJpaTest
public class ProdutRepositoryCorrectionTests {

	@Autowired
	private ProductRepository productRepository;
	
	private long countTotalProduct;
	private long countCategory3Products;
	private Pageable pageable;

	@BeforeEach
	void setUp() throws Exception {
		this.countTotalProduct = 25;
		this.countCategory3Products = 23;
		this.pageable = PageRequest.of(0, 10);
	}

	@Test
	public void findShouldReturnAllProductWhenCategoryNotInformed() {

		List<Category> categories = null;

		Page<Product> result = this.productRepository.find(categories, "", this.pageable);

		Assertions.assertFalse(result.isEmpty());

		Assertions.assertEquals(this.countTotalProduct, result.getTotalElements());
	}
	
	@Test
	public void findShouldReturnOnlySelectecCategoryWhenCategoryInformed() {

		List<Category> categories = new ArrayList<Category>();
		categories.add(new Category(3L, null));

		Page<Product> result = this.productRepository.find(categories, "", this.pageable);

		Assertions.assertFalse(result.isEmpty());

		Assertions.assertEquals(this.countCategory3Products, result.getTotalElements());
	}
}