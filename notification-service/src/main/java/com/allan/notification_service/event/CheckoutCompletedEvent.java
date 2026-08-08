package com.allan.notification_service.event;

import java.math.BigDecimal;

public record CheckoutCompletedEvent(
        Long orderId,
        int userId,
        String email,
        BigDecimal totalAmount,
        String status
) {
}