package com.allan.notification_service.kafka.consumer;

import com.allan.notification_service.event.CheckoutCompletedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterConsumer {

    @KafkaListener(
            topics = "checkout-completed.DLT",
            groupId = "notification-dlt-service"
    )
    public void consume(CheckoutCompletedEvent event) {

        System.out.println("Message moved to DLT");
        System.out.println("Order ID: " + event.orderId());
        System.out.println("Customer: " + event.email());
        System.out.println("Total: $" + event.totalAmount());
        System.out.println("Status: " + event.status());
    }
}