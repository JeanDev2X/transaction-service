package bank.transaction.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditPaymentRequest {
	private String payerDocumentNumber; // Quien paga
    private String productType;         // "LOAN" o "CREDIT_CARD"
    private String productNumber;       // Número del préstamo o tarjeta
    private BigDecimal amount;
}
