package com.allan.shopping_cart_api.repository;

import com.allan.shopping_cart_api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE Product p
            SET p.stockQuantity = p.stockQuantity - :quantity
            WHERE p.id = :productId
              AND p.stockQuantity >= :quantity
            """)
    int deductStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity
    );
}