package com.lab.bankslip.application.command.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankSlipDto {
    private String customerId;
    private String payerName;
    private String payerDocument;
    private String beneficiaryName;
    private String beneficiaryDocument;
    private String documentNumber;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String webhookUrl;
}
