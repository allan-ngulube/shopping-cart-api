package com.allan.shopping_cart_api.controller;

import com.allan.shopping_cart_api.dto.CreateUserRequest;
import com.allan.shopping_cart_api.dto.UserResponse;
import com.allan.shopping_cart_api.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }
}