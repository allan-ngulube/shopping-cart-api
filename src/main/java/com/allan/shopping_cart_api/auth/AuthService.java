package com.allan.shopping_cart_api.auth;

import com.allan.shopping_cart_api.dto.LoginResponse;
import com.allan.shopping_cart_api.entity.User;
import com.allan.shopping_cart_api.exception.EmailAlreadyExistsException;
import com.allan.shopping_cart_api.exception.InvalidCredentialsException;
import com.allan.shopping_cart_api.refreshToken.RefreshToken;
import com.allan.shopping_cart_api.refreshToken.RefreshTokenRequest;
import com.allan.shopping_cart_api.refreshToken.RefreshTokenService;
import com.allan.shopping_cart_api.repository.UserRepository;
import com.allan.shopping_cart_api.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(
                    "An account with this email already exists"
            );
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        String accessToken =
                jwtService.generateToken(user.getEmail());

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken.getToken()
        );
    }

    public LoginResponse refreshAccessToken(
            RefreshTokenRequest request
    ) {
        RefreshToken refreshToken =
                refreshTokenService.validateRefreshToken(
                        request.getRefreshToken()
                );

        User user = refreshToken.getUser();

        String newAccessToken =
                jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                newAccessToken,
                refreshToken.getToken()
        );
    }

    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revokeRefreshToken(
                request.getRefreshToken()
        );
    }
}