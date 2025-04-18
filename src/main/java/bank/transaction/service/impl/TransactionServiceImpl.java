package bank.transaction.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import bank.transaction.dto.AccountResponse;
import bank.transaction.dto.CommissionReportResponse;
import bank.transaction.dto.CreditResponse;
import bank.transaction.dto.DebitCardDTO;
import bank.transaction.dto.DebitCardPaymentRequest;
import bank.transaction.dto.TransactionDTO;
import bank.transaction.dto.TransactionRequest;
import bank.transaction.dto.TransactionResponse;
import bank.transaction.dto.TransferRequest;
import bank.transaction.entity.Transaction;
import bank.transaction.repository.TransactionRepository;
import bank.transaction.service.TransactionService;

import java.math.BigDecimal;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionServiceImpl implements TransactionService{
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	private static final String ACCOUNT_SERVICE_URL = "http://localhost:8021/accounts";
	private static final String DEBIT_SERVICE_URL = "http://localhost:8021/debit-cards";
	//en el metodo, paycredit, falta implementar la actualizacion de la cuenta de credito,validar
	private static final String CREDIT_SERVICE_URL = "http://localhost:8022/loans";
	
	private static final int MAX_FREE_TRANSACTIONS = 5;
	private static final BigDecimal COMMISSION_AMOUNT = new BigDecimal("1.50");
	
	@Autowired
    private TransactionRepository transactionRepository;

	@Override
	public Mono<TransactionResponse> deposit(TransactionRequest transactionRequest) {
		WebClient webClient = webClientBuilder.build();

	    return transactionRepository.countByAccountNumberAndDateBetween(
	            transactionRequest.getAccountNumber(),
	            getStartOfMonth(),
	            getEndOfMonth()
	        )
	        .flatMap(transactionCount -> {
	            BigDecimal commission = (transactionCount >= MAX_FREE_TRANSACTIONS) ? COMMISSION_AMOUNT : BigDecimal.ZERO;

	            return webClient
	                    .get()
	                    .uri(ACCOUNT_SERVICE_URL + "/by-account-number/{accountNumber}", transactionRequest.getAccountNumber())
	                    .retrieve()
	                    .bodyToMono(AccountResponse.class)
	                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")))
	                    .flatMap(accountResponse -> {
	                        // Calcular el monto final después de aplicar la comisión (si la hay)
	                        BigDecimal finalAmount = transactionRequest.getAmount().subtract(commission);

	                        accountResponse.setBalance(accountResponse.getBalance().add(finalAmount));

	                        // Actualizar saldo
	                        return webClient
	                                .put()
	                                .uri(ACCOUNT_SERVICE_URL + "/{id}", accountResponse.getId())
	                                .bodyValue(accountResponse)
	                                .retrieve()
	                                .bodyToMono(AccountResponse.class)
	                                .flatMap(updatedAccount -> {
	                                    // Guardar transacción
	                                    Transaction transaction = new Transaction();
	                                    transaction.setType("DEPOSIT");
	                                    transaction.setProductType("ACCOUNT");
	                                    transaction.setAmount(transactionRequest.getAmount());
	                                    transaction.setCommission(commission);
	                                    transaction.setAccountNumber(transactionRequest.getAccountNumber());
	                                    transaction.setDate(LocalDateTime.now());

	                                    return transactionRepository.save(transaction)
	                                            .map(savedTransaction -> new TransactionResponse(
	                                                    savedTransaction.getId(),
	                                                    savedTransaction.getAccountNumber(),
	                                                    savedTransaction.getType(),
	                                                    savedTransaction.getProductType(),
	                                                    savedTransaction.getAmount(),
	                                                    savedTransaction.getDate(),
	                                                    savedTransaction.getCommission(),
	                                                    savedTransaction.getSourceAccountNumber(),
	                                                    savedTransaction.getDestinationAccountNumber(),
	                                                    savedTransaction.getCardNumber()
	                                            ));
	                                });
	                    });
	        });
	}

	@Override
	public Mono<TransactionResponse> withdraw(Transaction transactionRequest) {
		WebClient webClient = webClientBuilder.build();

	    return transactionRepository.countByAccountNumberAndDateBetween(
	            transactionRequest.getAccountNumber(),
	            getStartOfMonth(),
	            getEndOfMonth()
	        )
	        .flatMap(transactionCount -> {
	            BigDecimal commission = (transactionCount >= MAX_FREE_TRANSACTIONS) ? COMMISSION_AMOUNT : BigDecimal.ZERO;

	            return webClient
	                    .get()
	                    .uri(ACCOUNT_SERVICE_URL + "/by-account-number/{accountNumber}", transactionRequest.getAccountNumber())
	                    .retrieve()
	                    .bodyToMono(AccountResponse.class)
	                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")))
	                    .flatMap(account -> {
	                        BigDecimal finalAmount = transactionRequest.getAmount().add(commission); // retirar más si hay comisión

	                        if (account.getBalance().compareTo(finalAmount) < 0) {
	                            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds"));
	                        }

	                        account.setBalance(account.getBalance().subtract(finalAmount));

	                        return webClient
	                                .put()
	                                .uri(ACCOUNT_SERVICE_URL + "/{id}", account.getId())
	                                .bodyValue(account)
	                                .retrieve()
	                                .bodyToMono(AccountResponse.class)
	                                .flatMap(updatedAccount -> {
	                                    transactionRequest.setType("WITHDRAW");
	                                    transactionRequest.setProductType("ACCOUNT");
	                                    transactionRequest.setDate(LocalDateTime.now());
	                                    transactionRequest.setCommission(commission);

	                                    return transactionRepository.save(transactionRequest)
	                                            .map(savedTransaction -> new TransactionResponse(
	                                                    savedTransaction.getId(),
	                                                    savedTransaction.getAccountNumber(),
	                                                    savedTransaction.getType(),
	                                                    savedTransaction.getProductType(),
	                                                    savedTransaction.getAmount(),
	                                                    savedTransaction.getDate(),
	                                                    savedTransaction.getCommission(),
	                                                    savedTransaction.getSourceAccountNumber(),
	                                                    savedTransaction.getDestinationAccountNumber(),
	                                                    savedTransaction.getCardNumber()
	                                            ));
	                                });
	                    });
	        });
	}

	@Override
	public Mono<TransactionResponse> payCredit(Transaction transactionRequest) {
		WebClient webClient = webClientBuilder.build();
	    System.out.println("numero de credito = "+transactionRequest.getCreditNumber());
	    // Validar que el creditNumber esté presente en la transacción
	    if (transactionRequest.getCreditNumber() == null || transactionRequest.getCreditNumber().isEmpty()) {
	        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit ID is required for credit payments"));
	    }

	    // Contamos las transacciones realizadas este mes para verificar si se supera el límite sin comisión
	    return transactionRepository.countByAccountNumberAndDateBetween(
	            transactionRequest.getAccountNumber(),
	            getStartOfMonth(),
	            getEndOfMonth()
	        )
	        .flatMap(transactionCount -> {
	            // Si superó el límite, aplicamos la comisión
	            BigDecimal commission = (transactionCount >= MAX_FREE_TRANSACTIONS) ? COMMISSION_AMOUNT : BigDecimal.ZERO;

	            // Obtener la cuenta bancaria asociada para verificar saldo
	            return webClient
	                .get()
	                .uri(ACCOUNT_SERVICE_URL + "/by-account-number/{accountNumber}", transactionRequest.getAccountNumber())
	                .retrieve()
	                .bodyToMono(AccountResponse.class)
	                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")))
	                .flatMap(account -> {
	                    BigDecimal finalAmount = transactionRequest.getAmount().add(commission);

	                    // Verificar si hay fondos suficientes en la cuenta bancaria para realizar el pago (con comisión si aplica)
	                    if (account.getBalance().compareTo(finalAmount) < 0) {
	                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds"));
	                    }

	                    // Debitar el monto del saldo de la cuenta bancaria
	                    account.setBalance(account.getBalance().subtract(finalAmount));

	                    // Actualizar la cuenta bancaria con el nuevo saldo
	                    return webClient
	                        .put()
	                        .uri(ACCOUNT_SERVICE_URL + "/{id}", account.getId())
	                        .bodyValue(account)
	                        .retrieve()
	                        .bodyToMono(AccountResponse.class)
	                        .flatMap(updatedAccount -> {
	                        	System.out.println("numero de credito = "+transactionRequest.getCreditNumber());
	                            // Aquí se realiza la conexión al servicio de créditos para actualizar el saldo del crédito
	                            return webClient
	                                .get()
	                                .uri(CREDIT_SERVICE_URL + "/{creditNumber}", transactionRequest.getCreditNumber())
	                                .retrieve()
	                                .bodyToMono(CreditResponse.class)
	                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Credit not found")))
	                                .flatMap(creditResponse -> {
	                                	
	                                    // Reducir el saldo del crédito por el monto de la transacción
	                                    creditResponse.setBalance(creditResponse.getBalance().subtract(transactionRequest.getAmount()));

	                                    // Actualizar el crédito en el servicio de créditos
	                                    return webClient
	                                    		.post()
	                                        .uri(CREDIT_SERVICE_URL + "/{creditNumber}/pay?amount=" + transactionRequest.getAmount(),
	                                                transactionRequest.getCreditNumber())
	                                        .bodyValue(creditResponse)
	                                        .retrieve()
	                                        .bodyToMono(CreditResponse.class)
	                                        .flatMap(updatedCredit -> {
	                                            // Guardamos la transacción en el repositorio de transacciones
	                                            transactionRequest.setType("PAYMENT");
	                                            transactionRequest.setProductType("CREDIT");
	                                            transactionRequest.setDate(LocalDateTime.now());
	                                            transactionRequest.setCommission(commission);

	                                            // Guardamos la transacción en el repositorio de transacciones
	                                            return transactionRepository.save(transactionRequest)
	                                                .map(savedTransaction -> new TransactionResponse(
	                                                    savedTransaction.getId(),
	                                                    savedTransaction.getAccountNumber(),
	                                                    savedTransaction.getType(),
	                                                    savedTransaction.getProductType(),
	                                                    savedTransaction.getAmount(),
	                                                    savedTransaction.getDate(),
	                                                    savedTransaction.getCommission(), // Aseguramos que se guarde la comisión aplicada
	                                                    savedTransaction.getSourceAccountNumber(),
	                                                    savedTransaction.getDestinationAccountNumber(),
	                                                    savedTransaction.getCardNumber()
	                                                ));
	                                        });
	                                });
	                        });
	                });
	        });
	}
	
	@Override
	public Mono<TransactionResponse> payCreditCard(Transaction transactionRequest) {
	    WebClient webClient = webClientBuilder.build();

	    if (transactionRequest.getCreditNumber() == null || transactionRequest.getCreditNumber().isEmpty()) {
	        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card number is required"));
	    }

	    return transactionRepository.countByAccountNumberAndDateBetween(
	            transactionRequest.getAccountNumber(),
	            getStartOfMonth(),
	            getEndOfMonth()
	        )
	        .flatMap(transactionCount -> {
	            BigDecimal commission = (transactionCount >= MAX_FREE_TRANSACTIONS) ? COMMISSION_AMOUNT : BigDecimal.ZERO;

	            return webClient
	                .get()
	                .uri(ACCOUNT_SERVICE_URL + "/by-account-number/{accountNumber}", transactionRequest.getAccountNumber())
	                .retrieve()
	                .bodyToMono(AccountResponse.class)
	                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")))
	                .flatMap(account -> {
	                    BigDecimal finalAmount = transactionRequest.getAmount().add(commission);

	                    if (account.getBalance().compareTo(finalAmount) < 0) {
	                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds"));
	                    }

	                    account.setBalance(account.getBalance().subtract(finalAmount));

	                    return webClient
	                        .put()
	                        .uri(ACCOUNT_SERVICE_URL + "/{id}", account.getId())
	                        .bodyValue(account)
	                        .retrieve()
	                        .bodyToMono(AccountResponse.class)
	                        .flatMap(updatedAccount -> {
	                            // Aquí se llama al endpoint del credit-service para pagar la tarjeta
	                            return webClient
	                                .post()
	                                .uri(CREDIT_SERVICE_URL + "/credit-cards/" + transactionRequest.getCreditNumber() + "/pay?amount=" + transactionRequest.getAmount()
	                                	)
	                                .retrieve()
	                                .bodyToMono(CreditResponse.class)
	                                .flatMap(updatedCard -> {
	                                    transactionRequest.setType("CREDIT_CARD_PAYMENT");
	                                    transactionRequest.setProductType("CREDIT_CARD");
	                                    transactionRequest.setDate(LocalDateTime.now());
	                                    transactionRequest.setCommission(commission);
	                                    transactionRequest.setCardNumber(transactionRequest.getCreditNumber());

	                                    return transactionRepository.save(transactionRequest)
	                                        .map(savedTransaction -> new TransactionResponse(
	                                            savedTransaction.getId(),
	                                            savedTransaction.getAccountNumber(),
	                                            savedTransaction.getType(),
	                                            savedTransaction.getProductType(),
	                                            savedTransaction.getAmount(),
	                                            savedTransaction.getDate(),
	                                            savedTransaction.getCommission(),
	                                            savedTransaction.getSourceAccountNumber(),
	                                            savedTransaction.getDestinationAccountNumber(),
	                                            savedTransaction.getCardNumber()
	                                        ));
	                                });
	                        });
	                });
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

    private LocalDateTime getStartOfMonth() {
        return LocalDate.now().withDayOfMonth(1).atStartOfDay();
    }

    private LocalDateTime getEndOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
    }
    
    @Override
    public Mono<TransactionResponse> transfer(TransferRequest transferRequest) {
    	WebClient webClient = webClientBuilder.build();

        if (transferRequest.getSourceAccountNumber() == null || transferRequest.getDestinationAccountNumber() == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both source and destination account numbers are required"));
        }

        return transactionRepository.countByAccountNumberAndDateBetween(
                transferRequest.getSourceAccountNumber(),
                getStartOfMonth(),
                getEndOfMonth()
            )
            .flatMap(transactionCount -> {
                BigDecimal commission = (transactionCount >= MAX_FREE_TRANSACTIONS) ? COMMISSION_AMOUNT : BigDecimal.ZERO;
                BigDecimal totalAmount = transferRequest.getAmount().add(commission);

                // Obtener cuentas origen y destino
                Mono<AccountResponse> sourceAccountMono = webClient
                        .get()
                        .uri(ACCOUNT_SERVICE_URL + "/by-account-number/{accountNumber}", transferRequest.getSourceAccountNumber())
                        .retrieve()
                        .bodyToMono(AccountResponse.class);

                Mono<AccountResponse> destinationAccountMono = webClient
                        .get()
                        .uri(ACCOUNT_SERVICE_URL + "/by-account-number/{accountNumber}", transferRequest.getDestinationAccountNumber())
                        .retrieve()
                        .bodyToMono(AccountResponse.class);

                return Mono.zip(sourceAccountMono, destinationAccountMono)
                    .flatMap(tuple -> {
                        AccountResponse sourceAccount = tuple.getT1();
                        AccountResponse destinationAccount = tuple.getT2();

                        if (sourceAccount.getBalance().compareTo(totalAmount) < 0) {
                            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds in source account"));
                        }

                        // Actualizar saldos
                        sourceAccount.setBalance(sourceAccount.getBalance().subtract(totalAmount));
                        destinationAccount.setBalance(destinationAccount.getBalance().add(transferRequest.getAmount()));

                        // Actualizar cuentas
                        return webClient
                                .put()
                                .uri(ACCOUNT_SERVICE_URL + "/{id}", sourceAccount.getId())
                                .bodyValue(sourceAccount)
                                .retrieve()
                                .bodyToMono(AccountResponse.class)
                                .flatMap(updatedSource -> webClient
                                    .put()
                                    .uri(ACCOUNT_SERVICE_URL + "/{id}", destinationAccount.getId())
                                    .bodyValue(destinationAccount)
                                    .retrieve()
                                    .bodyToMono(AccountResponse.class)
                                )
                                .flatMap(updatedDestination -> {
                                    Transaction transaction = new Transaction();
                                    transaction.setProductType("ACCOUNT");
                                    transaction.setType("TRANSFER");
                                    transaction.setAmount(transferRequest.getAmount());
                                    transaction.setSourceAccountNumber(transferRequest.getSourceAccountNumber());
                                    transaction.setDestinationAccountNumber(transferRequest.getDestinationAccountNumber());
                                    transaction.setDate(LocalDateTime.now());
                                    transaction.setCommission(commission);
                                    

                                    return transactionRepository.save(transaction)
                                            .map(savedTransaction -> new TransactionResponse(
                                                    savedTransaction.getId(),
                                                    savedTransaction.getAccountNumber(),
                                                    savedTransaction.getType(),
                                                    savedTransaction.getProductType(),
                                                    savedTransaction.getAmount(),
                                                    savedTransaction.getDate(),
                                                    savedTransaction.getCommission(),
                                                    savedTransaction.getSourceAccountNumber(),
                                                    savedTransaction.getDestinationAccountNumber(),
                                                    savedTransaction.getCardNumber()
                                            ));
                                });
                    });
            });
    }
    
    @Override
    public Flux<CommissionReportResponse> getCommissionReport(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByDateBetween(startDate, endDate)
                .filter(tx -> tx.getCommission() != null && tx.getCommission().compareTo(BigDecimal.ZERO) > 0)
                .groupBy(Transaction::getProductType)
                .flatMap(groupedFlux -> groupedFlux
                    .map(Transaction::getCommission)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .map(total -> new CommissionReportResponse(groupedFlux.key(), total))
                );
    }

    @Override
    public Mono<List<TransactionResponse>> getTransactionsByProductTypeAndDateRange(String productType, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        return transactionRepository
            .findByProductTypeAndDateBetween(productType.toUpperCase(), start, end)
            .map(tx -> new TransactionResponse(
                tx.getId(),
                tx.getAccountNumber(),
                tx.getType(),
                tx.getProductType(),
                tx.getAmount(),
                tx.getDate(),
                tx.getCommission(),
                tx.getSourceAccountNumber(),
                tx.getDestinationAccountNumber(),
                tx.getCardNumber()
            ))
            .collectList();
    }

	@Override
	public Mono<TransactionResponse> payWithDebitCard(DebitCardPaymentRequest request) {
		WebClient webClient = webClientBuilder.build();

	    return webClient.get()
	        .uri(DEBIT_SERVICE_URL + "/{cardNumber}", request.getCardNumber())
	        .retrieve()
	        .bodyToMono(DebitCardDTO.class)
	        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Debit card not found")))
	        .flatMap(card -> {
	            if (card.getAccountNumber() == null || card.getAccountNumber().isEmpty()) {
	                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card not linked to account"));
	            }

	            return webClient.get()
	                .uri(ACCOUNT_SERVICE_URL + "/by-account-number/{accountNumber}", card.getAccountNumber())
	                .retrieve()
	                .bodyToMono(AccountResponse.class)
	                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")))
	                .flatMap(account -> transactionRepository.countByAccountNumberAndDateBetween(
	                        account.getAccountNumber(), getStartOfMonth(), getEndOfMonth())
	                    .flatMap(txCount -> {
	                        BigDecimal commission = txCount >= MAX_FREE_TRANSACTIONS ? COMMISSION_AMOUNT : BigDecimal.ZERO;
	                        BigDecimal total = request.getAmount().add(commission);

	                        if (account.getBalance().compareTo(total) < 0) {
	                            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds"));
	                        }

	                        account.setBalance(account.getBalance().subtract(total));

	                        return webClient.put()
	                            .uri(ACCOUNT_SERVICE_URL + "/{id}", account.getId())
	                            .bodyValue(account)
	                            .retrieve()
	                            .bodyToMono(AccountResponse.class)
	                            .flatMap(updatedAccount -> {
	                                Transaction tx = Transaction.builder()
	                                        .accountNumber(account.getAccountNumber())
	                                        .type("DEBIT_CARD_PAYMENT")
	                                        .productType("ACCOUNT")
	                                        .amount(request.getAmount())
	                                        .commission(commission)
	                                        .date(LocalDateTime.now())
	                                        .cardNumber(request.getCardNumber()) // Aquí lo agregamos
	                                        .build();

	                                return transactionRepository.save(tx)
	                                    .map(saved -> new TransactionResponse(
	                                            saved.getId(),
	                                            saved.getAccountNumber(),
	                                            saved.getType(),
	                                            saved.getProductType(),
	                                            saved.getAmount(),
	                                            saved.getDate(),
	                                            saved.getCommission(),
	                                            saved.getSourceAccountNumber(),
	                                            saved.getDestinationAccountNumber(),
	                                            saved.getCardNumber() // Agrega esta línea
	                                    ));
	                            });
	                    }));
	        });
	}

	@Override
	public Flux<TransactionDTO> getLastMovementsByCardNumbers(List<String> cardNumbers) {
		return Flux.fromIterable(cardNumbers)
	            .flatMap(cardNumber -> transactionRepository.findByCardNumberOrderByDateDesc(cardNumber)
	                    .take(10)) // Toma los 10 últimos por cada tarjeta
	            .sort((tx1, tx2) -> tx2.getDate().compareTo(tx1.getDate())) // Ordena globalmente por fecha descendente
	            .take(10) // Toma los últimos 10 en total (combinados)
	            .map(tx -> new TransactionDTO(
	                    tx.getAccountNumber(),
	                    tx.getType(),
	                    tx.getAmount(),
	                    tx.getDate(),
	                    tx.getCommission(),
	                    tx.getProductType(),
	                    tx.getSourceAccountNumber(),
	                    tx.getDestinationAccountNumber(),
	                    tx.getCardNumber()
	            ));
	}
    
}
