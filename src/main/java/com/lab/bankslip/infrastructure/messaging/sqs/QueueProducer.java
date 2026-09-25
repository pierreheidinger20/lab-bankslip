package com.lab.bankslip.infrastructure.messaging.sqs;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import io.awspring.cloud.sqs.operations.SqsTemplate;

@Component
@RequiredArgsConstructor
public class QueueProducer {

    private final SqsTemplate sqsTemplate;

    public void send(String queue, String message) {
        sqsTemplate.send(
            to -> to
                .queue(queue)
                .payload(message)
        );
    }
}