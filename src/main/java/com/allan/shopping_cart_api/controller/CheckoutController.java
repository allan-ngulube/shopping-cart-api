package com.allan.shopping_cart_api.controller;

import com.allan.shopping_cart_api.dto.OrderResponse;
import com.allan.shopping_cart_api.entity.Order;
import com.allan.shopping_cart_api.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<OrderResponse> checkout(
            @PathVariable Long userId
    ) {

        Order order = checkoutService.checkout(userId);

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount()
        );

        return ResponseEntity.ok(response);
    }
}