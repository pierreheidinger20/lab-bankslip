package com.lab.bankslip.infrastructure.service.webhook.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BankSlipWebhookPayload(
                Long id,
                String customerId,
                String barcode,
                String digitableLine,
                BigDecimal amount,
                LocalDate dueDate,
                String base64file) {
        public BankSlipWebhookPayload(String customerId) {
                this(null, customerId, null, null, BigDecimal.ZERO, null, null);
        }
}
