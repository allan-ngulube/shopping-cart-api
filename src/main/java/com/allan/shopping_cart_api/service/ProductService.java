package com.allan.shopping_cart_api.service;

import com.allan.shopping_cart_api.entity.Product;
import com.allan.shopping_cart_api.exception.ProductNotFoundException;
import com.allan.shopping_cart_api.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {

        product.setIsActive(true);
        product.setCreatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}