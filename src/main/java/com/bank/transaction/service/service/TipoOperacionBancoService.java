package com.bank.transaction.service.service;

import com.bank.transaction.service.entity.TipoOperacionBanco;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TipoOperacionBancoService {
	
	Flux<TipoOperacionBanco> findAllTipoproducto();
	Mono<TipoOperacionBanco> findByIdTipoProducto(String id);
	Mono<TipoOperacionBanco> saveTipoProducto(TipoOperacionBanco tipoProducto);
	Mono<Void> deleteTipo(TipoOperacionBanco tipoProducto);
	
}
