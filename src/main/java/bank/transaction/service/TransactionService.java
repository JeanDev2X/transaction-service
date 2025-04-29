package bank.transaction.service;

import java.time.LocalDate;
import java.util.List;

import bank.transaction.dto.CommissionReportResponse;
import bank.transaction.dto.CreditPaymentRequest;
import bank.transaction.dto.DebitCardPaymentRequest;
import bank.transaction.dto.TransactionDTO;
import bank.transaction.dto.TransactionRequest;
import bank.transaction.dto.TransactionResponse;
import bank.transaction.dto.TransferRequest;
import bank.transaction.entity.Transaction;
import bank.transaction.event.WalletEvent;
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
    public Mono<TransactionResponse> payCreditCard(Transaction transactionRequest);
    Mono<List<TransactionResponse>> getTransactionsByProductTypeAndDateRange(String productType, LocalDate startDate, LocalDate endDate);
    public Mono<TransactionResponse> payWithDebitCard(DebitCardPaymentRequest request);
    Flux<TransactionDTO> getLastMovementsByCardNumbers(List<String> cardNumbers);
    Mono<Transaction> processCreditPayment(CreditPaymentRequest request);
    public void registerYankiLoad(WalletEvent event);
    void processLoadFromCard(WalletEvent event);
}
