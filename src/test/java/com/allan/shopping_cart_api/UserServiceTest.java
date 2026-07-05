package com.allan.shopping_cart_api;

import com.allan.shopping_cart_api.dto.CreateUserRequest;
import com.allan.shopping_cart_api.dto.UserResponse;
import com.allan.shopping_cart_api.entity.User;
import com.allan.shopping_cart_api.repository.UserRepository;
import com.allan.shopping_cart_api.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserSuccessfully() {

        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("Allan");
        request.setLastName("Ngulube");
        request.setEmail("allan@test.com");
        request.setPasswordHash("password123");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        User savedUser = User.builder()
                .id(1)
                .first_name("Allan")
                .last_name("Ngulube")
                .email("allan@test.com")
                .passwordHash("password123")
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        // Act
        UserResponse response = userService.createUser(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Allan", response.getFirstName());
        assertEquals("Ngulube", response.getLastName());
        assertEquals("allan@test.com", response.getEmail());

        verify(userRepository).findByEmail("allan@test.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("allan@test.com");

        User existingUser = User.builder()
                .id(1)
                .email("allan@test.com")
                .build();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.createUser(request)
        );

        assertEquals("Email already exists", exception.getMessage());

        verify(userRepository).findByEmail("allan@test.com");
        verify(userRepository, never()).save(any(User.class));
    }
}