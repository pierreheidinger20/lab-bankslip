package com.lab.bankslip.infrastructure.service.webhook.dto;

public record BankSlipWebhookDto(
        BankSlipWebhookPayload payload,
        BankSlipWebhookError error
) {
}

