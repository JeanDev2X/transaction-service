package bank.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
	private String accountNumber;
    private String type;
    private BigDecimal amount;
    private LocalDateTime date;
    private BigDecimal commission;
    private String productType;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private String cardNumber;
}
