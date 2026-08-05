package com.allan.shopping_cart_api.refreshToken;

import com.allan.shopping_cart_api.entity.User;
import jakarta.persistence.*;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Secure random refresh token sent to the client
    @Column(nullable = false, unique = true)
    private String token;

    // When this refresh token expires
    @Column(nullable = false)
    private Instant expiresAt;

    // Indicates whether the token has been revoked (e.g., logout)
    @Column(nullable = false)
    private boolean revoked = false;

    // Timestamp when this refresh token was created
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // Many refresh tokens can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Automatically set the creation time before inserting into the database
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    // Returns true if the current time is after the expiration time
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    // A refresh token is valid only if it hasn't expired and hasn't been revoked
    public boolean isValid() {
        return !revoked && !isExpired();
    }
}

