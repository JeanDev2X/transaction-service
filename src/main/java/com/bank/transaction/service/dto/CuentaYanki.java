package com.bank.transaction.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CuentaYanki {
	
	private String id;
	private String dni;
	private String numeroCelular;	
	private TipoCuentaBanco tipoProducto;
	private String fecha_afiliacion;
	private String fecha_caducidad;
	private double saldo;
	private String codigoBanco;
	
	
	
}










