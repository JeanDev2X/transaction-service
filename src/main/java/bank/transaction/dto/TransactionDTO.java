package bank.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import bank.transaction.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
	private String accountNumber;
	private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDate date;
    private BigDecimal commission;
    private String productType;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private String cardNumber;
}
