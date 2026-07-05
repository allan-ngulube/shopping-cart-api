package com.allan.shopping_cart_api.exception;

public class ActiveCartNotFoundException extends RuntimeException {

    public ActiveCartNotFoundException(Long userId) {
        super("Active cart not found for user with id " + userId);
    }
}