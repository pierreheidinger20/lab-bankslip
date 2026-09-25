package com.lab.bankslip.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BankSlipRequestDto(

        @NotBlank(message = "customerId is required")
        String customerId,

        @NotBlank(message = "payerName is required")
        String payerName,

        @NotBlank(message = "payerDocument is required")
        String payerDocument,

        @NotBlank(message = "beneficiaryName is required")
        String beneficiaryName,

        @NotBlank(message = "beneficiaryDocument is required")
        String beneficiaryDocument,

        @NotBlank(message = "documentNumber is required")
        String documentNumber,

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "dueDate is required")
        LocalDate dueDate,

        String webhookUrl
) {
}
