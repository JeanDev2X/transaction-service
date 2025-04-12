package com.bank.transaction.service.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TransferRequest {
	private String sourceAccountNumber;  // Cuenta de origen
    private String destinationAccountNumber; // Cuenta de destino
    private BigDecimal amount;  // Monto a transferir
}
