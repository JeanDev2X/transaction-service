package bank.transaction.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import bank.transaction.dto.CommissionReportResponse;
import bank.transaction.dto.CreditPaymentRequest;
import bank.transaction.dto.DebitCardPaymentRequest;
import bank.transaction.dto.TransactionDTO;
import bank.transaction.dto.TransactionRequest;
import bank.transaction.dto.TransactionResponse;
import bank.transaction.dto.TransferRequest;
import bank.transaction.entity.Transaction;
import bank.transaction.service.TransactionService;
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
    public Mono<TransactionResponse> withdraw(@RequestBody Transaction transaction) {
        return transactionService.withdraw(transaction);
    }
    //pagar prestamo
    @PostMapping("/pay-credit")
    public Mono<TransactionResponse> payCredit(@RequestBody Transaction transaction) {
        // Validar que el creditNumber esté presente en la transacción
    	System.out.println("numero de credito = "+transaction.getCreditNumber());
    	System.out.println("transaction = "+transaction);
        if (transaction.getCreditNumber() == null || transaction.getCreditNumber().isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit ID is required for credit payments"));
        }

        // Delegar la lógica al service, que gestionará la comisión y demás
        return transactionService.payCredit(transaction);
    }
    //pagar tarjeta
    @PostMapping("/pay-credit-card")
    public Mono<TransactionResponse> payCreditCard(@RequestBody Transaction transactionRequest) {
        return transactionService.payCreditCard(transactionRequest);
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
    
    @PostMapping("/transfer")
    public Mono<TransactionResponse> transfer(@RequestBody TransferRequest transferRequest) {
        return transactionService.transfer(transferRequest);
    }
    
    @GetMapping("/commission-report")
    public Flux<CommissionReportResponse> getCommissionReport(
            @RequestParam("start") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate start,
            @RequestParam("end") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate end) {

        return transactionService.getCommissionReport(start, end);
    }
    
    @GetMapping("/by-product-and-date")
    public Mono<List<TransactionResponse>> getByProductAndDateRange(
            @RequestParam String productType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return transactionService.getTransactionsByProductTypeAndDateRange(productType, startDate, endDate);
    }
    
    @PostMapping("/last-movements/by-cards")
    public Flux<TransactionDTO> getLastMovementsByCardNumbers(@RequestBody List<String> cardNumbers) {
        return transactionService.getLastMovementsByCardNumbers(cardNumbers);
    }
    //pagar con tarjeta de debito
    @PostMapping("/pay-debit-card")
    public Mono<TransactionResponse> payWithDebitCard(@RequestBody DebitCardPaymentRequest request) {
        return transactionService.payWithDebitCard(request);
    }
    //pagar credito de terceros
    @PostMapping("/credit-payment")
    public Mono<Transaction> payCredit(@RequestBody CreditPaymentRequest request) {
        return transactionService.processCreditPayment(request);
    }
    
}
