package com.allan.shopping_cart_api.kafka.event;

import java.math.BigDecimal;

public record CheckoutCompletedEvent(
        Long orderId,
        int userId,
        String email,
        BigDecimal totalAmount,
        String status
) {
}