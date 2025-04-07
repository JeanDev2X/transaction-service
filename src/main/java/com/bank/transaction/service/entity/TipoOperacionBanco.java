package com.bank.transaction.service.entity;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TipoOperacionBanco {

	@NotEmpty
	private String id;
	@NotEmpty
	private String descripcion;
	
	public TipoOperacionBanco() {

	}

	public TipoOperacionBanco(String idTipo, String descripcion) {
		this.id = idTipo;
		this.descripcion = descripcion;
	}
	
}
