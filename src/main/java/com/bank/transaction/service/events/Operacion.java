package com.bank.transaction.service.events;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Operacion {
	
	@NotEmpty
	private String dni;
	@NotEmpty
	private String numeroCelularOrigen;
	@NotEmpty
	private String numeroCelularDestino;
	@NotEmpty
	private double montoPago;
	@NotEmpty
	private String tipoOperacion;

}
