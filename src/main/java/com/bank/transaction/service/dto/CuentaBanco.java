package com.bank.transaction.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CuentaBanco {
	
	private String id;
	private String dni;
	private String numeroCuenta;	
	private TipoCuentaBanco tipoProducto;
	private String fecha_afiliacion;
	private String fecha_caducidad;
	private double saldo;
	private String usuario;
	private String clave;
	private String codigoBanco;
	
	
	
}










