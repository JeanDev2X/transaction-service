package com.bank.transaction.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bank.transaction.service.dto.TransactionRequest;
import com.bank.transaction.service.dto.TransactionResponse;
import com.bank.transaction.service.entity.Transaction;
import com.bank.transaction.service.service.TransactionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
	
	@Autowired
    private TransactionService transactionService;

	@PostMapping("/deposit")
    public Mono<TransactionResponse> deposit(@RequestBody TransactionRequest transactionRequest) {
        return transactionService.deposit(transactionRequest);
    }

    @PostMapping("/withdraw")
    public Mono<Transaction> withdraw(@RequestBody Transaction transaction) {
        return transactionService.withdraw(transaction);
    }

    @PostMapping("/pay-credit")
    public Mono<Transaction> payCredit(@RequestBody Transaction transaction) {
        return transactionService.payCredit(transaction);
    }

//    @GetMapping("/balance/account/{accountNumber}")
//    public Mono<BigDecimal> getAccountBalance(@PathVariable String accountNumber) {
//        return transactionService.getAccountBalance(accountNumber);
//    }
//
//    @GetMapping("/balance/credit/{creditNumber}")
//    public Mono<BigDecimal> getCreditBalance(@PathVariable String creditNumber) {
//        return transactionService.getCreditBalance(creditNumber);
//    }

    @GetMapping("/movements/account/{accountNumber}")
    public Flux<TransactionResponse> getAccountMovements(@PathVariable String accountNumber) {
        return transactionService.getMovementsByAccount(accountNumber);
    }

    @GetMapping("/movements/credit/{creditNumber}")
    public Flux<TransactionResponse> getCreditMovements(@PathVariable String creditNumber) {
        return transactionService.getMovementsByCredit(creditNumber);
    }
    
}
