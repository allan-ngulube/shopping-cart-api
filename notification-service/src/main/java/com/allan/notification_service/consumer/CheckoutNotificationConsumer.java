package com.allan.notification_service.consumer;

import com.allan.notification_service.event.CheckoutCompletedEvent;
import com.allan.notification_service.service.NotificationService;
import com.allan.notification_service.service.ProcessedOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CheckoutNotificationConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(CheckoutNotificationConsumer.class);

    private final ProcessedOrderService processedOrderService;
    private final NotificationService notificationService;

    public CheckoutNotificationConsumer(
            ProcessedOrderService processedOrderService,
            NotificationService notificationService
    ) {
        this.processedOrderService = processedOrderService;
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "checkout-completed",
            groupId = "notification-service"
    )
    public void consume(CheckoutCompletedEvent event) {

        if (processedOrderService.isProcessed(event.orderId())) {

            log.warn(
                    "Duplicate checkout event ignored: orderId={}",
                    event.orderId()
            );

            return;
        }

        log.info(
                "Checkout event received: orderId={}, userId={}, email={}, total={}, status={}",
                event.orderId(),
                event.userId(),
                event.email(),
                event.totalAmount(),
                event.status()
        );

        String message =
                "Order " + event.orderId()
                        + " confirmed for " + event.email()
                        + ". Total: $" + event.totalAmount();

        notificationService.sendNotification(
                event.email(),
                message
        );

        processedOrderService.markProcessed(event.orderId());

        log.info(
                "Checkout notification processed successfully: orderId={}",
                event.orderId()
        );
    }
}