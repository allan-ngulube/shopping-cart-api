package com.allan.shopping_cart_api.repository;

import com.allan.shopping_cart_api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
