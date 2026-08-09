package com.allan.shopping_cart_api.kafka.producer;

import com.allan.shopping_cart_api.kafka.event.CheckoutCompletedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CheckoutEventProducer {

    private static final String TOPIC = "checkout-completed";

    private final KafkaTemplate<String, CheckoutCompletedEvent> kafkaTemplate;

    public CheckoutEventProducer(
            KafkaTemplate<String, CheckoutCompletedEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(CheckoutCompletedEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.orderId().toString(),
                event
        );
    }
}