package com.allan.shopping_cart_api.repository;

import com.allan.shopping_cart_api.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}