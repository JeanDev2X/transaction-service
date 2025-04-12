package com.bank.transaction.service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommissionReportResponse {
	private String productType; // ACCOUNT o CREDIT
    private BigDecimal totalCommission;
}
