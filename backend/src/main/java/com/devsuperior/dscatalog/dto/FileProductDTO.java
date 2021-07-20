package com.devsuperior.dscatalog.dto;

import java.io.Serializable;
import java.util.Base64;
import com.devsuperior.dscatalog.entities.FileProduct;

public class FileProductDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

    private String name;

    private String originalFilename;

    private String encodedImage;

	private String extensao;

	private Long size;

	public FileProductDTO() {

	}

	public FileProductDTO(FileProduct entity) {

		this.id = entity.getId();
		this.name = entity.getName();
		this.originalFilename = entity.getOriginalFilename();
		this.extensao = entity.getExtensao();
		this.size = entity.getSize();

		if (entity.getContent() != null) {
			this.encodedImage = Base64.getEncoder().encodeToString(entity.getContent()).toString();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public String getEncodedImage() {
		return encodedImage;
	}

	public void setEncodedImage(String encodedImage) {
		this.encodedImage = encodedImage;
	}

	public String getExtensao() {
		return extensao;
	}

	public void setExtensao(String extensao) {
		this.extensao = extensao;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileProductDTO other = (FileProductDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}