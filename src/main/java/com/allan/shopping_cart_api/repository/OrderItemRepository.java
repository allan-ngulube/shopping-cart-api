package com.allan.shopping_cart_api.repository;

import com.allan.shopping_cart_api.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}