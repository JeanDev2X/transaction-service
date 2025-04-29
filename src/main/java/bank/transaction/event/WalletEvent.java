package bank.transaction.event;

import java.math.BigDecimal;

import bank.transaction.dto.WalletDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletEvent {
	private String eventType;    // "LOAD_FROM_CARD"
    private WalletDTO wallet;    // Datos de la billetera
    private String fromPhoneNumber;   // De quién envía dinero
    private String toPhoneNumber;     // A quién recibe dinero
    private BigDecimal amount;        // Monto de dinero
    private String debitCardNumber;   // Para cargas desde tarjeta de débito
}
