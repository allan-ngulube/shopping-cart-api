package com.allan.notification_service.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate
    ) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, exception) ->
                                new TopicPartition(
                                        record.topic() + ".DLT",
                                        record.partition()
                                )
                );

        /*
         * Wait 2 seconds between failures.
         * Retry 3 times after the original attempt.
         *
         * Total attempts:
         * 1 original attempt + 3 retries = 4 attempts.
         */
        FixedBackOff fixedBackOff = new FixedBackOff(
                2000L,
                3L
        );

        return new DefaultErrorHandler(
                recoverer,
                fixedBackOff
        );
    }
}