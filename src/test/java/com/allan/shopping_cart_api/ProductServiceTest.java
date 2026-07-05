package com.allan.shopping_cart_api;

import com.allan.shopping_cart_api.entity.Product;
import com.allan.shopping_cart_api.repository.ProductRepository;
import com.allan.shopping_cart_api.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAllProducts_shouldReturnProducts() {
        Product product = new Product();
        product.setId(1L);
        product.setName("MacBook Pro");
        product.setDescription("Apple laptop");
        product.setPrice(new BigDecimal("2499.99"));
        product.setStockQuantity(10);
        product.setIsActive(true);

        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> products = productService.getAllProducts();

        assertEquals(1, products.size());
        assertEquals("MacBook Pro", products.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void createProduct_shouldSaveAndReturnProduct() {
        Product product = new Product();
        product.setName("iPhone");
        product.setDescription("Apple phone");
        product.setPrice(new BigDecimal("999.99"));
        product.setStockQuantity(20);
        product.setIsActive(true);

        when(productRepository.save(product)).thenReturn(product);

        Product savedProduct = productService.createProduct(product);

        assertNotNull(savedProduct);
        assertEquals("iPhone", savedProduct.getName());
        verify(productRepository, times(1)).save(product);
    }
}