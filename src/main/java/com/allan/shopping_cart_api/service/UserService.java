package com.allan.shopping_cart_api.service;

import com.allan.shopping_cart_api.dto.CreateUserRequest;
import com.allan.shopping_cart_api.dto.UserResponse;
import com.allan.shopping_cart_api.entity.User;
import com.allan.shopping_cart_api.repository.UserRepository;
import org.springframework.stereotype.Service;
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .first_name(request.getFirstName())
                .last_name(request.getLastName())
                .email(request.getEmail())
                .passwordHash(request.getPasswordHash())
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .firstName(savedUser.getFirst_name())
                .lastName(savedUser.getLast_name())
                .email(savedUser.getEmail())
                .build();
    }
}