package bank.transaction.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class AccountResponse {
	private String id;
	private String accountNumber;
    private BigDecimal balance;
    private String documentNumber;
    private String type;
    private List<String> holders;
    private List<String> signers;
}
