package com.lab.bankslip.application.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import io.awspring.cloud.sqs.annotation.SqsListener;

import com.lab.bankslip.application.command.Mediator;
import com.lab.bankslip.application.command.ProcessBankSlipCommand;
import com.lab.bankslip.application.command.dto.SaveBankSlipDto;
import com.lab.bankslip.infrastructure.messaging.sqs.QueueConsumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankSlipQueueConsumer implements QueueConsumer {

    private final Mediator _mediator;
    private final ObjectMapper _objectMapper;

    @Override
    @SqsListener(queueNames = "${bankslip.sqs.queue-url}", factory = "bankSlipQueueListenerFactory")
    public void receive(String message) {
        try {
            log.info("Creating bank slip with message: {}", message);
            SaveBankSlipDto saveBankSlipDto = _objectMapper.readValue(message, SaveBankSlipDto.class);
            _mediator.send(new ProcessBankSlipCommand(saveBankSlipDto));
        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
        }
    }
}