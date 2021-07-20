package com.devsuperior.dscatalog.services;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.devsuperior.dscatalog.dto.FileProductDTO;
import com.devsuperior.dscatalog.entities.FileProduct;
import com.devsuperior.dscatalog.repositories.FileProductRepository;

@Service
public class FileProductService {

	@Autowired
	private FileProductRepository fileProductRepository;

	@Transactional(readOnly = true)
	public FileProduct findByIdForSaveProduct(Long id) {

		Optional<FileProduct> entity = this.fileProductRepository.findById(id);

		return entity.isPresent() ? entity.get() : null;
	}

	@Transactional
	public FileProductDTO insert(MultipartFile file) throws Exception {

		FileProduct entity = new FileProduct();

		try {

			entity.setContent(file.getBytes());
			entity.setName(file.getName());
			entity.setOriginalFilename(file.getOriginalFilename());
			entity.setExtensao(file.getContentType());
			entity.setSize(file.getSize());

			entity = this.fileProductRepository.save(entity);

		} catch (IOException e) {
			throw new Exception("Ocorreu um erro ao salvar o arquivo.");
		}

		return new FileProductDTO(entity);
	}

	public FileProductDTO update(Long id, MultipartFile file) throws Exception  {

		FileProduct entity = this.fileProductRepository.getOne(id);

		try {

		   entity.setName(file.getName());
		   entity.setOriginalFilename(file.getOriginalFilename());
		   entity.setContent(file.getBytes());
		   entity.setExtensao(file.getContentType());
		   entity.setSize(file.getSize());

		   entity = this.fileProductRepository.save(entity);

	    } catch (IOException e) {
		   throw new Exception("Ocorreu um erro ao salvar o arquivo.");
	    }

		return new FileProductDTO(entity);
	}

	@Transactional(readOnly = true)
	public void deleteFileWithoutProduct() {

		Iterable<FileProduct> listFile = this.fileProductRepository.listFileWithoutProduct();

		if (listFile != null) {
			this.fileProductRepository.deleteInBatch(listFile);
		}
	}
}