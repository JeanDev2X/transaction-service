package bank.transaction.entity;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wallets")
public class Wallet {
	@Id
    private String id;
    private String documentType;
    private String documentNumber;
    private String phoneNumber;
    private String imei;
    private String email;
    private BigDecimal balance;
    private String linkedDebitCardNumber;
}
