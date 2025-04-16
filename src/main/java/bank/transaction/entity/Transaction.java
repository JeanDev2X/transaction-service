package bank.transaction.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private String productType; // Tipo de producto: "ACCOUNT" o "CREDIT"
    private String type; // Tipo de transacción: "DEPOSIT", "WITHDRAWAL", "PAYMENT"
    private BigDecimal amount; // Monto de la transacción
    private String creditNumber; // En caso de ser una transacción de pago de crédito, se usa el ID del crédito
    private LocalDateTime date = LocalDateTime.now(); // Fecha de la transacción
    private BigDecimal commission;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
}
