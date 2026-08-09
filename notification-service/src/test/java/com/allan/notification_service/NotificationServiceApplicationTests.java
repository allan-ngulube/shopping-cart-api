package com.allan.notification_service;

import com.allan.notification_service.consumer.CheckoutNotificationConsumer;
import com.allan.notification_service.event.CheckoutCompletedEvent;
import com.allan.notification_service.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class NotificationServiceApplicationTests {

	@Autowired
	private CheckoutNotificationConsumer checkoutNotificationConsumer;

	@MockitoBean
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
						"allan@example.com",
						new BigDecimal("29.99"),
						"CONFIRMED"
				);

		checkoutNotificationConsumer.consume(event);
		checkoutNotificationConsumer.consume(event);

		verify(notificationService, times(1))
				.sendNotification(
						eq("allan@example.com"),
						anyString()
				);
	}
}