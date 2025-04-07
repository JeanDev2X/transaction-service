package com.bank.transaction.service.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.transaction.service.entity.OperacionCuentaBanco;
import com.bank.transaction.service.service.OperacionBancoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/api/operacionBancaria") //  OperCuentasCorrientes
@RestController
public class OperacionBancoControllers {

	@Autowired
	private OperacionBancoService productoService;

	@GetMapping
	public Mono<ResponseEntity<Flux<OperacionCuentaBanco>>> findAll() {
		return Mono.just(
				ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(productoService.findAllOperacion())

		);
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<OperacionCuentaBanco>> viewId(@PathVariable String id) {
		return productoService.findByIdOperacion(id)
				.map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	/*
	Todas las cuentas bancarias tendrán un número máximo de transacciones (depósitos y retiros) 
	que no cobrará comisión y superado ese número se cobrará comisión por cada transacción realizada.
	EL número máximo de transacciones libres de comisiones 20.
	
	*/	
	//----------------------------------------------
	//RETIROS - TRANSACCION : UPDATE-CUENTAS-SALDO - 2 TRACCIONES COBRA COMISION(RETIRO O DEPOSITO) - TIPO TARGETA
	@PostMapping("/retiro")
	public Mono<OperacionCuentaBanco> operacionRetiro(@RequestBody OperacionCuentaBanco operacion) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionRetiro(operacion);
	}
	
	//DEPOSITO - 2 TRACCIONES COBRA COMISION(RETIRO O DEPOSITO)
	@PostMapping("/deposito")
	public Mono<OperacionCuentaBanco> operacionDeposito(@RequestBody OperacionCuentaBanco producto) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionDeposito(producto);
	}
	
	/*
	Implementar las transferencias bancarias entre cuentas del mismo cliente y cuentas a terceros del mismo banco
	*/
	//OPERACION TRANSFERENCIA DE CUENTA A CUENTA
	@PostMapping("/cuentaACuenta")
	public Mono<OperacionCuentaBanco> operacionCuentaACuenta(@RequestBody OperacionCuentaBanco oper) {
		//System.out.println(producto.toString());
		return productoService.operacionCuentaCuenta(oper);
	}
	
	//PAGO DE CUENTA CREDITO CON UNA CUENTA DE BANCO
	@PostMapping("/CuentaBancoACredito") //Cuenta_a_Credito
	public Mono<OperacionCuentaBanco> operacionCuentaBancoACredito(@RequestBody OperacionCuentaBanco producto) {		
		return productoService.saveOperacionCuentaCuentaCredito(producto);
	}
	
	/*
	El sistema debe permitir consultar todos los movimientos de un producto bancario que tiene un cliente
	*/
	//LISTA LOS CLIENTE CON OP-BANCO - 
	@GetMapping("/dni/{dni}")
	public Flux<OperacionCuentaBanco> operacionesBancoCliente(@PathVariable String dni) {
		Flux<OperacionCuentaBanco> oper = productoService.findAllOperacionByDniCliente(dni);
		return oper;
	}
	
	@GetMapping("/comision/{dni}/fecha/{fecha}")//yyyy-MM-dd
	public Flux<OperacionCuentaBanco> operacionesComision(@PathVariable String dni,@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fecha) {
		Flux<OperacionCuentaBanco> oper = productoService.findComision(dni,fecha);
		
		return oper;
	}
		
}



