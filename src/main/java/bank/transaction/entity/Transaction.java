package bank.transaction.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class Transaction {
	@Id
    private String id;
	private String accountNumber; // Número de cuenta asociado a la transacción (puede ser una cuenta bancaria o un crédito)
    private String productType; // Tipo de producto: "ACCOUNT", "CREDIT" y "CREDIT_CARD"
 // Tipo de transacción: "DEPOSIT", "WITHDRAWAL", "PAYMENT","CREDIT_CARD_PAYMENT","DEBIT_CARD_PAYMENT"
    private TransactionType transactionType;
    private String phoneNumber;
    private String documentNumber;
    private BigDecimal amount; // Monto de la transacción
    private String creditNumber; // En caso de ser una transacción de pago de crédito, se usa el ID del crédito
    private LocalDate transactionDate = LocalDate.now(); // Fecha de la transacción
    private BigDecimal commission;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private String cardNumber;// Número de tarjeta (crédito o débito)
    private String description;
}
