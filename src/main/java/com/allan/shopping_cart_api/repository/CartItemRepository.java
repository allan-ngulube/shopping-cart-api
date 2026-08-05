package com.allan.shopping_cart_api.repository;

import com.allan.shopping_cart_api.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductId(
            Long cartId,
            Long productId
    );

    @Query("""
            SELECT ci
            FROM CartItem ci
            JOIN FETCH ci.product
            WHERE ci.cart.id = :cartId
            """)
    List<CartItem> findByCartIdWithProduct(
            @Param("cartId") Long cartId
    );

    List<CartItem> findByCartId(Long cartId);
}