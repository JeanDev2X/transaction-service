package com.bank.transaction.service.service;

import java.time.LocalDate;

import com.bank.transaction.service.dto.CommissionReportResponse;
import com.bank.transaction.service.dto.TransactionRequest;
import com.bank.transaction.service.dto.TransactionResponse;
import com.bank.transaction.service.dto.TransferRequest;
import com.bank.transaction.service.entity.Transaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<TransactionResponse> withdraw(Transaction transaction);
    Mono<TransactionResponse> payCredit(Transaction transaction);
    Mono<TransactionResponse> deposit(TransactionRequest transactionRequest);

//    Mono<BigDecimal> getAccountBalance(String accountNumber);
//    Mono<BigDecimal> getCreditBalance(String creditNumber);
    Flux<TransactionResponse> getMovementsByAccount(String accountNumber);
    Flux<TransactionResponse> getMovementsByCredit(String creditNumber);
    public Mono<TransactionResponse> transfer(TransferRequest transferRequest);
    Flux<CommissionReportResponse> getCommissionReport(LocalDate startDate, LocalDate endDate);

}
