package com.bank.transaction.service.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.bank.transaction.service.entity.TipoOperacionBanco;

public interface TipoOperacionBancoDao extends ReactiveMongoRepository<TipoOperacionBanco, String> {

}
