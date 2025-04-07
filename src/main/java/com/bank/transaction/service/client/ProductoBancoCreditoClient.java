package com.bank.transaction.service.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bank.transaction.service.dto.CuentaBanco;

import reactor.core.publisher.Mono;

@Service
public class ProductoBancoCreditoClient {

	private static final Logger log = LoggerFactory.getLogger(CuentaBanco.class);
	
	@Autowired
	@Qualifier("productoBancoCredito")
	private WebClient productoBancoCreditoClient;
	
	//producto credito
	public Mono<CuentaBanco> despositoBancario(Double monto,String cuenta_destino,String codigo_bancario_destino) {
		
		Map<String, String> pathVariable = new HashMap<String,String>();
		pathVariable.put("monto",Double.toString(monto));
		pathVariable.put("numero_cuenta",cuenta_destino);
		pathVariable.put("codigo_bancario",codigo_bancario_destino);
		
		System.out.println("MONTO " + monto);
		System.out.println("BANCO " + codigo_bancario_destino);
		
		log.info("Actualizando: cuenta origen ---> deposito-credito: ->> "+ cuenta_destino +" monto : " + monto + "banco destino" + codigo_bancario_destino);	
		
		return productoBancoCreditoClient.put()
				   .uri("/pago/{numero_cuenta}/{monto}/{codigo_bancario}",pathVariable)
				   .accept(MediaType.APPLICATION_JSON)
				   .contentType(MediaType.APPLICATION_JSON)
				   .retrieve()
				   .bodyToMono(CuentaBanco.class);		
	}
	
	public Mono<CuentaBanco> findByNumeroCuentaCredito(String numero_cuenta,String codigo_bancario) {
		
		log.info("NUMERO CTA CREDITO : "+ numero_cuenta + " BANCO DESTINO : " + codigo_bancario);
		
		Map<String, String> pathVariable = new HashMap<String,String>();
		pathVariable.put("numero_cuenta",numero_cuenta);
		pathVariable.put("codigo_bancario",codigo_bancario);
		
		return productoBancoCreditoClient.get()
				.uri("/numero_cuenta/{numero_cuenta}/{codigo_bancario}",pathVariable)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(CuentaBanco.class);
		    	
	}
	
}
