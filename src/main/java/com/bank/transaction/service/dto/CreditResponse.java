package com.bank.transaction.service.dto;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreditResponse {
	@Id
    private String id;
    private String documentNumber;
    private String type; // PERSONAL, BUSINESS, CREDIT_CARD
    private BigDecimal amount;//monto
    private BigDecimal balance;//saldo
    private int termMonths;//plazo Meses
    private BigDecimal interestRate;//tasa de interés
}
