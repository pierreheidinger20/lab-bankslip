package com.lab.bankslip.infrastructure.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;

@Configuration
public class SqsConfig {

    @Bean
    public SqsMessageListenerContainerFactory<Object> bankSlipQueueListenerFactory(
            SqsAsyncClient sqsAsyncClient) {

        return SqsMessageListenerContainerFactory
                .builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(options -> options
                        .maxConcurrentMessages(10)
                )
                .build();
    }
}