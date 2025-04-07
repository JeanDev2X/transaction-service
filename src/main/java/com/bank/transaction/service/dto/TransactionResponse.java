package com.bank.transaction.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
	private String id;            // ID de la transacción
	private String accountNumber; // Número de cuenta relacionado
	private String type;          // Tipo de transacción (DEPOSIT, WITHDRAWAL, etc.)
	private BigDecimal amount;    // Monto de la transacción
    private LocalDateTime date; // Fecha y hora de la transacción
}
