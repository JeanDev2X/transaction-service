package com.bank.transaction.service.repository;

import java.util.Date;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.bank.transaction.service.entity.OperacionCuentaBanco;

import reactor.core.publisher.Flux;

public interface OperacionBancoDao extends ReactiveMongoRepository<OperacionCuentaBanco, String> {
	
	@Query("{ 'dni' : ?0 , 'cuenta_origen' : ?1, 'codigo_bancario_origen' : ?2 }")
	Flux<OperacionCuentaBanco> consultaMovimientos(String dni, String cuenta, String codigo_bancario);
	
	//---REPORTES
	@Query("{ 'dni' : ?0 }")
	Flux<OperacionCuentaBanco> viewDniCliente(String dni);
	
	@Query("{ 'dni': ?0, 'comision': { $gt: 0 }, 'fechaOperacion': { $gt: ?1 } }")
    Flux<OperacionCuentaBanco> findByDniAndComisionGreaterThanZero(String dni,Date fecha);
	
}
