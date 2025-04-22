package bank.transaction.dto;

import java.util.List;

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
    private List<String> accountNumbers; // ← actualizado, cuentas asociadas
    private String description;
    private String customerDocumentNumber; // opcional si quieres devolverlo también
}
