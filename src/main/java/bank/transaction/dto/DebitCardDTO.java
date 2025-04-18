package bank.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardDTO {
	private String cardNumber;
    private String accountNumber; // Número de cuenta asociada
}
