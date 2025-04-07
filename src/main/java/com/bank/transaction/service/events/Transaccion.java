package com.bank.transaction.service.events;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class Transaccion {
	private String id;
	private String numTransaccion;
	private Orden orden;
	@NotEmpty
	private String origen;
	@NotEmpty
	private String destino;
	private double montoPago;//monto a pagar por la cantidad de bitconind
}
