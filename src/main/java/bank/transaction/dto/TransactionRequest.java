package bank.transaction.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
	private String accountNumber;  // El número de cuenta para realizar el depósito
    private BigDecimal amount;     // Monto a depositar
}
