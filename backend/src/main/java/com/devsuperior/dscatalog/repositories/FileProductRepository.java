package com.devsuperior.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.devsuperior.dscatalog.entities.FileProduct;

@Repository
public interface FileProductRepository extends JpaRepository<FileProduct, Long> {

	@Query("SELECT obj FROM FileProduct obj WHERE obj.id NOT IN (SELECT p.fileProduct.id FROM Product p WHERE p.fileProduct.id = obj.id) ")
	Iterable<FileProduct> listFileWithoutProduct();

}
