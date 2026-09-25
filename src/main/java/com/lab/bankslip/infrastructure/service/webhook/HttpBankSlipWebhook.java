package com.lab.bankslip.infrastructure.service.webhook;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import com.lab.bankslip.domain.model.BankSlip;
import com.lab.bankslip.domain.model.BankSlipFailed;
import com.lab.bankslip.domain.service.BankSlipWebhook;
import com.lab.bankslip.infrastructure.service.webhook.dto.BankSlipWebhookDto;
import com.lab.bankslip.infrastructure.service.webhook.dto.BankSlipWebhookError;
import com.lab.bankslip.infrastructure.service.webhook.dto.BankSlipWebhookPayload;

@Component
@RequiredArgsConstructor
public class HttpBankSlipWebhook implements BankSlipWebhook {

    private final RestClient restClient;

    @Override
    public void notify(BankSlip bankSlip) {

        if (bankSlip.getWebhookUrl() == null
                || bankSlip.getWebhookUrl().isBlank()) {
            return;
        }
        restClient.post()
                .uri(bankSlip.getWebhookUrl())
                .body(new BankSlipWebhookDto(
                        new BankSlipWebhookPayload(
                                bankSlip.getId(),
                                bankSlip.getCustomerId(),
                                bankSlip.getBarcode(),
                                bankSlip.getDigitableLine(),
                                bankSlip.getAmount(),
                                bankSlip.getDueDate(),
                                bankSlip.getBase64file()),
                        null))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void notifyFailure(BankSlipFailed BankSlipFailed,String error) {
        if (BankSlipFailed.getWebhookUrl() == null
                || BankSlipFailed.getWebhookUrl().isBlank()) {
            return;
        }
        BankSlipWebhookPayload payload = new BankSlipWebhookPayload(BankSlipFailed.getPayload());
        restClient.post()
                .uri(BankSlipFailed.getWebhookUrl())
                .body(new BankSlipWebhookDto(
                        payload,
                        new BankSlipWebhookError(error)))
                .retrieve()
                .toBodilessEntity();
    }
}