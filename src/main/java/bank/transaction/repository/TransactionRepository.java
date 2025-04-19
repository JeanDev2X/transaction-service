package bank.transaction.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import bank.transaction.entity.Transaction;
import bank.transaction.entity.TransactionType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionRepository extends ReactiveCrudRepository<Transaction, String>{
	// Encuentra todas las transacciones de una cuenta bancaria
    Flux<Transaction> findByAccountNumber(String accountNumber);

    // Encuentra todas las transacciones de un crédito específico
    Flux<Transaction> findByCreditNumber(String creditNumber);

    // Encuentra las transacciones de acuerdo con el tipo (DEPOSIT, WITHDRAWAL, PAYMENT)
    Flux<Transaction> findByTransactionType(TransactionType transactionType);

    // Encuentra las transacciones por tipo y número de cuenta
    Flux<Transaction> findByTransactionTypeAndAccountNumber(TransactionType transactionType, String accountNumber);
    
    Flux<Transaction> findByAccountNumberAndProductType(String accountNumber, String productType);
    
    Mono<Long> countByAccountNumberAndTransactionDateBetween(String accountNumber, LocalDate start, LocalDate end);
    
    Flux<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
    
    Flux<Transaction> findByProductTypeAndTransactionDateBetween(String productType, LocalDate start, LocalDate end);
    
    Flux<Transaction> findByAccountNumberInOrderByTransactionDateDesc(List<String> accountNumbers);
    
    Flux<Transaction> findByCardNumberOrderByTransactionDateDesc(String cardNumber);
}
