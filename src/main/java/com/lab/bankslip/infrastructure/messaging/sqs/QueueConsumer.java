package com.lab.bankslip.infrastructure.messaging.sqs;

public interface QueueConsumer {
    void receive(String message);
}
