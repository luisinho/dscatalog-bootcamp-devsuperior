package com.devsuperior.dscatalog.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devsuperior.dscatalog.dto.FileProductDTO;
import com.devsuperior.dscatalog.services.FileProductService;

@RestController
@RequestMapping(value = "/files-product")
public class FileProductResource {

	@Autowired
	private FileProductService fileProductService;
	
	@PostMapping(value = "/image")
	public ResponseEntity<FileProductDTO> upload(@RequestParam("file") MultipartFile file) throws Exception{

		FileProductDTO dto = new FileProductDTO();

		try {

			this.fileProductService.deleteFileWithoutProduct();

			dto = this.fileProductService.insert(file);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Ocorreu um erro ao salvar o arquivo.");
		}

		return ResponseEntity.ok().body(dto);
	}
}
