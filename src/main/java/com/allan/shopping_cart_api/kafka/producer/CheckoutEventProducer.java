package com.allan.shopping_cart_api.kafka.producer;

import com.allan.shopping_cart_api.kafka.event.CheckoutCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CheckoutEventProducer {

    private static final Logger log =
            LoggerFactory.getLogger(CheckoutEventProducer.class);

    private static final String TOPIC = "checkout-completed";

    private final KafkaTemplate<String, CheckoutCompletedEvent> kafkaTemplate;

    public CheckoutEventProducer(
            KafkaTemplate<String, CheckoutCompletedEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(CheckoutCompletedEvent event) {

        log.info(
                "Publishing checkout event: orderId={}, topic={}",
                event.orderId(),
                TOPIC
        );

        kafkaTemplate.send(
                TOPIC,
                event.orderId().toString(),
                event
        ).whenComplete((result, ex) -> {

            if (ex != null) {
                log.error(
                        "Failed to publish checkout event: orderId={}",
                        event.orderId(),
                        ex
                );
            } else {
                log.info(
                        "Checkout event published successfully: orderId={}, partition={}, offset={}",
                        event.orderId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            }
        });
    }
}