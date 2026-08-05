package com.allan.shopping_cart_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Service responsible for validating JWTs and extracting claims.
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Read the Authorization header from the incoming request.
        String authorizationHeader =
                request.getHeader("Authorization");

        // If there is no Authorization header or it doesn't start with
        // "Bearer ", skip JWT authentication and continue processing.
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Remove "Bearer " from the header to get the raw JWT.
        String token = authorizationHeader.substring(7);

        // Authenticate only if:
        // 1. The JWT is valid.
        // 2. The user has not already been authenticated.
        if (jwtService.isTokenValid(token)
                && SecurityContextHolder.getContext()
                .getAuthentication() == null) {

            // Extract the user's email from the JWT.
            String email = jwtService.extractEmail(token);

            // Create an authenticated user with the USER role.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,                         // Principal
                            null,                          // No password stored
                            List.of(
                                    new SimpleGrantedAuthority("ROLE_USER")
                            )
                    );

            // Store the authenticated user in Spring Security's context.
            // Controllers and future filters can now access the authenticated user.
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }

        // Continue processing the remaining filters and eventually the controller.
        filterChain.doFilter(request, response);
    }
}