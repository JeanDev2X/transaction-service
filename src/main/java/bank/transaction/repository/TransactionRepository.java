package bank.transaction.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import bank.transaction.entity.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionRepository extends ReactiveCrudRepository<Transaction, String>{
	// Encuentra todas las transacciones de una cuenta bancaria
    Flux<Transaction> findByAccountNumber(String accountNumber);

    // Encuentra todas las transacciones de un crédito específico
    Flux<Transaction> findByCreditNumber(String creditNumber);

    // Encuentra las transacciones de acuerdo con el tipo (DEPOSIT, WITHDRAWAL, PAYMENT)
    Flux<Transaction> findByType(String type);

    // Encuentra las transacciones por tipo y número de cuenta
    Flux<Transaction> findByTypeAndAccountNumber(String type, String accountNumber);
    
    Flux<Transaction> findByAccountNumberAndProductType(String accountNumber, String productType);
    
    Mono<Long> countByAccountNumberAndDateBetween(String accountNumber, LocalDateTime start, LocalDateTime end);
    
    Flux<Transaction> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
