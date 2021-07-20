package com.devsuperior.dscatalog.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.FileProduct;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private FileProductService fileProductService;

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(Long categoryId, String name, PageRequest pageRequest) {

		List<Category> categories = (categoryId == 0) ? null :  Arrays.asList(this.categoryRepository.getOne(categoryId));

		Page<Product> page = this.productRepository.find(categories, name, pageRequest);

		this.productRepository.find(page.toList());

		return page.map(prod -> new ProductDTO(prod, prod.getCategories()));
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {

		Optional<Product> obj = this.productRepository.findById(id);

		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));

		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {

		Product entity = new Product();
		this.copyDtoToEntity(dto, entity);
		entity = this.productRepository.save(entity);

		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {

		try {

			Product entity = this.productRepository.getOne(id);
			this.copyDtoToEntity(dto, entity);
			entity = this.productRepository.save(entity);

			return new ProductDTO(entity);

		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}

	public void delete(Long id) {

		try {

			this.productRepository.deleteById(id);

		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}catch  (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		}
	}

	private void copyDtoToEntity(ProductDTO dto, Product entity) {

		FileProduct fileProduct = null;

		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setPrice(dto.getPrice());

		if (dto.getFileProduct() != null) {
			fileProduct = this.fileProductService.findByIdForSaveProduct(dto.getFileProduct().getId());
		}

		if (fileProduct != null && entity.getImgUrl() != null) {
			entity.setFileProduct(fileProduct);
			entity.setImgUrl(null);
		} else if(fileProduct != null && entity.getImgUrl() == null) {
			entity.setFileProduct(fileProduct);
		} else if(fileProduct == null && entity.getImgUrl() != null) {
			entity.setImgUrl(dto.getImgUrl());
		}

		entity.getCategories().clear();

		dto.getCategories().forEach(catDto -> {
			Category category = this.categoryRepository.getOne(catDto.getId());
			entity.getCategories().add(category);
		});
	}
}