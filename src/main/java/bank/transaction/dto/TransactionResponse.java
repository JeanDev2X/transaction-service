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
public class TransactionResponse {
	private String id;            // ID de la transacción
	private String accountNumber; // Número de cuenta relacionado
	private TransactionType transactionType;      // Tipo de transacción (DEPOSIT, WITHDRAWAL, etc.)
	private String productType;// ACCOUNT o CREDIT
	private BigDecimal amount;    // Monto de la transacción
    private LocalDate date; // Fecha y hora de la transacción
    private BigDecimal commission;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private String cardNumber;
}
