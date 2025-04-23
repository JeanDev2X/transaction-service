package bank.transaction.entity;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "transaction_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionConfigEntity {
	@Id
    private String id; // Necesario para poder actualizar
    private int maxFreeTransactions;
    private BigDecimal commissionAmount;
}
