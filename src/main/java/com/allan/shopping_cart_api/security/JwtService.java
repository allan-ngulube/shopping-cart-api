package com.allan.shopping_cart_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // Secret key used to sign and verify JWTs.
    // Loaded from application.properties.
    @Value("${jwt.secret}")
    private String secret;

    // Token expiration time in milliseconds.
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Creates the signing key used for generating
     * and validating JWT signatures.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generates a JWT for the authenticated user.
     *
     * The token contains:
     * - Subject (user email)
     * - Issue time
     * - Expiration time
     * - Digital signature
     */
    public String generateToken(String email) {

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + expiration)
                )
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the user's email (subject)
     * from a valid JWT.
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Validates the JWT.
     *
     * Returns true if:
     * - Signature is valid
     * - Token has not expired
     * - Token format is correct
     *
     * Otherwise returns false.
     */
    public boolean isTokenValid(String token) {

        try {
            extractClaims(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * Parses the JWT, verifies its signature,
     * and returns all claims stored inside it.
     *
     * Spring Security uses these claims to identify
     * the authenticated user.
     */
    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}