package com.bank.transaction.service.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.bank.transaction.service.entity.Transaction;
import com.bank.transaction.service.repository.TransactionRepository;
import com.bank.transaction.service.service.TransactionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.bank.transaction.service.dto.AccountResponse;
import com.bank.transaction.service.dto.CreditResponse;
import com.bank.transaction.service.dto.TransactionRequest;
import com.bank.transaction.service.dto.TransactionResponse;

@Service
public class TransactionServiceImpl implements TransactionService{
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	private static final String ACCOUNT_SERVICE_URL = "http://localhost:8021/accounts";
	private static final String CREDIT_SERVICE_URL = "http://localhost:8022/credits";
	
	@Autowired
    private TransactionRepository transactionRepository;

	@Override
	public Mono<TransactionResponse> deposit(TransactionRequest transactionRequest) {
	    WebClient webClient = webClientBuilder.build();

	    // Obtener la cuenta desde Account-Service usando el número de cuenta
	    return webClient
	            .get()
	            .uri(ACCOUNT_SERVICE_URL + "/by-account-number/{accountNumber}", transactionRequest.getAccountNumber())
	            .retrieve()
	            .bodyToMono(AccountResponse.class)  // Usamos AccountResponse en vez de Account
	            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")))
	            .flatMap(accountResponse -> {
	                // Actualizamos el saldo de la cuenta
	                accountResponse.setBalance(accountResponse.getBalance().add(transactionRequest.getAmount()));

	                // Actualizar la cuenta en Account-Service
	                return webClient
	                        .put()
	                        .uri(ACCOUNT_SERVICE_URL + "/{id}", accountResponse.getId())
	                        .bodyValue(accountResponse)
	                        .retrieve()
	                        .bodyToMono(AccountResponse.class);
	            })
	            .flatMap(updatedAccount -> {
	                // Guardar la transacción en Transaction-Service
	                Transaction transaction = new Transaction();
	                transaction.setType("DEPOSIT");
	                transaction.setProductType("ACCOUNT");
	                transaction.setAmount(transactionRequest.getAmount());
	                transaction.setAccountNumber(transactionRequest.getAccountNumber());
	                transaction.setDate(LocalDateTime.now());

	                return transactionRepository.save(transaction)
	                        .map(savedTransaction -> {
	                            // Retornar la respuesta de la transacción
	                            return new TransactionResponse(
	                                    savedTransaction.getId(),
	                                    savedTransaction.getAccountNumber(),
	                                    savedTransaction.getType(),
	                                    savedTransaction.getAmount(),
	                                    savedTransaction.getDate()
	                            );
	                        });
	            });
	}

	@Override
	public Mono<Transaction> withdraw(Transaction transaction) {
	    WebClient webClient = webClientBuilder.build();

	    // Buscar cuenta por número de cuenta
	    return webClient
	            .get()
	            .uri(ACCOUNT_SERVICE_URL + "/by-account-number/{accountNumber}", transaction.getAccountNumber())
	            .retrieve()
	            .bodyToMono(AccountResponse.class)
	            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada")))
	            .flatMap(account -> {
	                if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
	                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente"));
	                }

	                // Restar el monto del balance
	                account.setBalance(account.getBalance().subtract(transaction.getAmount()));

	                // Actualizar cuenta en Account Service
	                return webClient
	                        .put()
	                        .uri(ACCOUNT_SERVICE_URL + "/{id}", account.getId())
	                        .bodyValue(account)
	                        .retrieve()
	                        .bodyToMono(AccountResponse.class);
	            })
	            .flatMap(updatedAccount -> {
	                // Guardar la transacción
	                transaction.setType("WITHDRAWAL");
	                transaction.setProductType("ACCOUNT");
	                transaction.setDate(LocalDateTime.now());
	                return transactionRepository.save(transaction);
	            });
	}

	@Override
	public Mono<Transaction> payCredit(Transaction transaction) {
	    WebClient webClient = webClientBuilder.build();

	    // Obtener el crédito desde Credit-Service usando el ID (puedes cambiar a creditNumber si lo prefieres)
	    return webClient
	            .get()
	            .uri(CREDIT_SERVICE_URL + "/by-credit-number/{creditNumber}", transaction.getAccountNumber()) // Usa creditId aquí, no accountNumber
	            .retrieve()
	            .bodyToMono(CreditResponse.class)
	            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Crédito no encontrado")))
	            .flatMap(credit -> {
	                // Simulamos el pago disminuyendo el saldo adeudado
	                credit.setBalance(credit.getBalance().subtract(transaction.getAmount()));

	                // Actualizar el crédito en Credit-Service
	                return webClient
	                        .put()
	                        .uri(CREDIT_SERVICE_URL + "/{id}", credit.getId())
	                        .bodyValue(credit)
	                        .retrieve()
	                        .bodyToMono(CreditResponse.class);
	            })
	            .flatMap(updatedCredit -> {
	                // Guardar la transacción
	                transaction.setType("PAYMENT");
	                transaction.setProductType("CREDIT");
	                transaction.setDate(LocalDateTime.now());

	                return transactionRepository.save(transaction);
	            });
	}
	
//	@Override
//    public Mono<BigDecimal> getAccountBalance(String accountNumber) {
//        return transactionRepository.findByAccountNumberAndProductType(accountNumber, "ACCOUNT")
//                .map(tx -> "DEPOSIT".equals(tx.getType()) ? tx.getAmount() : tx.getAmount().negate())
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//
//    @Override
//    public Mono<BigDecimal> getCreditBalance(String creditNumber) {
//        return transactionRepository.findByAccountNumberAndProductType(creditNumber, "CREDIT")
//                .map(tx -> "PAYMENT".equals(tx.getType()) ? tx.getAmount().negate() : tx.getAmount())
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
	
	@Override
    public Flux<TransactionResponse> getMovementsByAccount(String accountNumber) {
        return transactionRepository.findByAccountNumberAndProductType(accountNumber, "ACCOUNT")
                .map(this::mapToResponse);
    }

    @Override
    public Flux<TransactionResponse> getMovementsByCredit(String creditNumber) {
        return transactionRepository.findByAccountNumberAndProductType(creditNumber, "CREDIT")
                .map(this::mapToResponse);
    }
    
    private TransactionResponse mapToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAccountNumber(transaction.getAccountNumber());
        response.setAmount(transaction.getAmount());
        response.setType(transaction.getType());
        response.setDate(transaction.getDate());
        return response;
    }

}
