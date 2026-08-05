package com.allan.shopping_cart_api.dto;

import java.math.BigDecimal;

public record OrderResponse(
        Long orderId,
        String status,
        BigDecimal totalAmount
) {
}