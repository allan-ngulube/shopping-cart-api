package com.allan.notification_service;

import com.allan.notification_service.consumer.CheckoutNotificationConsumer;
import com.allan.notification_service.event.CheckoutCompletedEvent;
import com.allan.notification_service.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class NotificationServiceApplicationTests {

	@Autowired
	private CheckoutNotificationConsumer checkoutNotificationConsumer;

	@Autowired
	private NotificationService notificationService;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldIgnoreDuplicateOrder() {

		CheckoutCompletedEvent event =
				new CheckoutCompletedEvent(
						7L,
						1,
						"rwanneallan@gmail.com",
						new BigDecimal("29.99"),
						"CONFIRMED"
				);

		checkoutNotificationConsumer.consume(event);
		checkoutNotificationConsumer.consume(event);
	}

	@Test
	void shouldSendEmail() {

		notificationService.sendNotification(
				"rwanneallan@gmail.com",
				"Test order confirmation from Spring Boot"
		);
	}

}