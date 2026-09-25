package com.lab.bankslip.application.command;

import com.lab.bankslip.application.command.dto.BankSlipDto;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.lab.bankslip.infrastructure.messaging.sqs.QueueProducer;
import com.lab.bankslip.application.command.dto.EnqueueBankSlipResultDto;

import lombok.RequiredArgsConstructor;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor 
public class EnqueueBankSlipCommandHandler implements CommandHandler<EnqueueBankSlipCommand, EnqueueBankSlipResultDto> {


    private final ObjectMapper _objectMapper;
    private final QueueProducer _queueProducer;

    @Value("${bankslip.sqs.queue-url}")
    private String queueUrl;

    @Override
    public EnqueueBankSlipResultDto handle(EnqueueBankSlipCommand command) {
        if (queueUrl.isBlank()) {
            throw new IllegalStateException("BANKSLIP_SQS_QUEUE_URL must be configured");
        }
        String requestId = UUID.randomUUID().toString();
        BankSlipDto bankSlipDto = command.bankSlipDto();
        try {
            BankSlipDto bankSlipDtoRequest = BankSlipDto.builder()
                    .customerId(bankSlipDto.getCustomerId())
                    .amount(bankSlipDto.getAmount())
                    .dueDate(bankSlipDto.getDueDate())
                    .webhookUrl(bankSlipDto.getWebhookUrl())
                    .payerName(bankSlipDto.getPayerName())
                    .payerDocument(bankSlipDto.getPayerDocument())
                    .beneficiaryName(bankSlipDto.getBeneficiaryName())
                    .beneficiaryDocument(bankSlipDto.getBeneficiaryDocument())
                    .documentNumber(bankSlipDto.getDocumentNumber())
                    .build();
            String body = _objectMapper.writeValueAsString(bankSlipDtoRequest);
            _queueProducer.send(queueUrl, body);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Could not serialize bank slip request", exception);
        }
        return new EnqueueBankSlipResultDto(requestId);
    }
}
