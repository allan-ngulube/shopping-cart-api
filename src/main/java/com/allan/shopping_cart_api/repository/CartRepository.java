package com.allan.shopping_cart_api.repository;

import com.allan.shopping_cart_api.entity.Cart;
import com.allan.shopping_cart_api.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndStatus(Long userId, String status);

}
