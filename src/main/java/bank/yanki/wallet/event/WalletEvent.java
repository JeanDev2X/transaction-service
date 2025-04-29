package bank.yanki.wallet.event;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class WalletEvent {
	private String eventType;
    private String fromPhoneNumber;
    private String toPhoneNumber;
    private BigDecimal amount;
    private String debitCardNumber;
}
