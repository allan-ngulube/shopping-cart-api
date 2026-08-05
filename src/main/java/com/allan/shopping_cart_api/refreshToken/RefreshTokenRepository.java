package com.allan.shopping_cart_api.refreshToken;

import com.allan.shopping_cart_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    List<RefreshToken> findAllByUserAndRevokedFalse(User user);

    Optional<RefreshToken> findByUser(User user);
    Optional<RefreshToken> findByToken(String token);
}