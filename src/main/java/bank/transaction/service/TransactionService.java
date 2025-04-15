package bank.transaction.service;

import java.time.LocalDate;

import bank.transaction.dto.CommissionReportResponse;
import bank.transaction.dto.TransactionRequest;
import bank.transaction.dto.TransactionResponse;
import bank.transaction.dto.TransferRequest;
import bank.transaction.entity.Transaction;
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
