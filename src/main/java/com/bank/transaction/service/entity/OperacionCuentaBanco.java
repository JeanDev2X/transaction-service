package com.bank.transaction.service.entity;

import java.util.Date;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document(collection ="Operaciones")
public class OperacionCuentaBanco {
	
	@Id
	@NotEmpty
	private String id;
	@NotEmpty
	private String dni;
	@NotEmpty
	private String cuenta_origen;
	@NotEmpty
	private String cuenta_destino;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaOperacion;
	@NotEmpty
	private TipoOperacionBanco tipoOperacion;
	@NotEmpty
	private double montoPago;
	
	private Double comision = 0.0;
	private String productoComision;
	
	@NotEmpty
	private String codigo_bancario_origen;
	
	@NotEmpty
	private String codigo_bancario_destino;

	public OperacionCuentaBanco() {
		
	}

	public OperacionCuentaBanco(String dni, String cuenta_origen,
			String cuenta_destino, Date fechaOperacion,TipoOperacionBanco tipoOperacion,
			double montoPago, Double comision,String productoComision,String codigo_bancario_origen,String codigo_bancario_destino) {
		this.dni = dni;
		this.cuenta_origen = cuenta_origen;
		this.cuenta_destino = cuenta_destino;
		this.fechaOperacion = fechaOperacion;
		this.tipoOperacion = tipoOperacion;
		this.montoPago = montoPago;
		this.comision = comision;
		this.productoComision = productoComision;
		this.codigo_bancario_origen = codigo_bancario_origen;
		this.codigo_bancario_destino = codigo_bancario_destino;
	}
	
	
}










