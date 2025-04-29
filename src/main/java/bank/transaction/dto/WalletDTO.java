package bank.transaction.dto;

import lombok.Data;

@Data
public class WalletDTO {
	private String id;
    private String phoneNumber;
    private String linkedDebitCardNumber;
}
