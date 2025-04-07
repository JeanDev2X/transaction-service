package com.bank.transaction.service.service;

import com.bank.transaction.service.dto.TransactionRequest;
import com.bank.transaction.service.dto.TransactionResponse;
import com.bank.transaction.service.entity.Transaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<Transaction> withdraw(Transaction transaction);
    Mono<Transaction> payCredit(Transaction transaction);
    Mono<TransactionResponse> deposit(TransactionRequest transactionRequest);

//    Mono<BigDecimal> getAccountBalance(String accountNumber);
//    Mono<BigDecimal> getCreditBalance(String creditNumber);
    Flux<TransactionResponse> getMovementsByAccount(String accountNumber);
    Flux<TransactionResponse> getMovementsByCredit(String creditNumber);
    
}
