package com.allan.shopping_cart_api.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
}