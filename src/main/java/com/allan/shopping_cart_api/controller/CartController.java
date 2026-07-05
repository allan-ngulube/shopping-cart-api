package com.allan.shopping_cart_api.controller;

import com.allan.shopping_cart_api.entity.Cart;
import com.allan.shopping_cart_api.entity.CartItem;
import com.allan.shopping_cart_api.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/users/{userId}")
    public Cart getOrCreateActiveCart(@PathVariable Long userId) {
        return cartService.getOrCreateActiveCart(userId);
    }
    @PostMapping("/users/{userId}/products/{productId}")
    public Cart addItemToCart(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam Integer quantity
    ) {
        return cartService.addItemToCart(userId, productId, quantity);
    }
    @GetMapping("/users/{userId}/items")
    public List<CartItem> getCartItems(@PathVariable Long userId) {
        return cartService.getCartItems(userId);
    }
}