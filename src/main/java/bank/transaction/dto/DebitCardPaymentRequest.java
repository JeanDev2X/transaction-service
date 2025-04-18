package bank.transaction.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardPaymentRequest {
	private String cardNumber;
    private BigDecimal amount;
}
