package com.bank.transaction.service.service;

import java.util.Date;

import com.bank.transaction.service.entity.OperacionCuentaBanco;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OperacionBancoService {

	Flux<OperacionCuentaBanco> findAllOperacion();
	Mono<OperacionCuentaBanco> findByIdOperacion(String id);
	Mono<OperacionCuentaBanco> saveOperacion(OperacionCuentaBanco operacion);
	//------------------------------------------------------------------------
	Mono<OperacionCuentaBanco> saveOperacionRetiro(OperacionCuentaBanco operacion);
	Mono<OperacionCuentaBanco> saveOperacionDeposito(OperacionCuentaBanco operacion);
	Mono<OperacionCuentaBanco> operacionCuentaCuenta(OperacionCuentaBanco operacion);
	Mono<OperacionCuentaBanco> saveOperacionCuentaCuentaCredito(OperacionCuentaBanco operacion);
	//----------------REPORTES
	Flux<OperacionCuentaBanco> findAllOperacionByDniCliente(String dni);
	Flux<OperacionCuentaBanco> findComision(String dni,Date fecha);
	
	//-------OPERACIONES YANKI
	void envioYanki(OperacionCuentaBanco operacion);
	void envioBoitcoin(OperacionCuentaBanco operacion);
}
