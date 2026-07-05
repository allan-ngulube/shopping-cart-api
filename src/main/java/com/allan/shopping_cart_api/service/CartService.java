package com.allan.shopping_cart_api.service;

import com.allan.shopping_cart_api.entity.Cart;
import com.allan.shopping_cart_api.entity.CartItem;
import com.allan.shopping_cart_api.entity.Product;
import com.allan.shopping_cart_api.entity.User;
import com.allan.shopping_cart_api.exception.ActiveCartNotFoundException;
import com.allan.shopping_cart_api.exception.ProductNotFoundException;
import com.allan.shopping_cart_api.exception.UserNotFoundException;
import com.allan.shopping_cart_api.repository.CartItemRepository;
import com.allan.shopping_cart_api.repository.CartRepository;
import com.allan.shopping_cart_api.repository.ProductRepository;
import com.allan.shopping_cart_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public Cart getOrCreateActiveCart(Long userId) {

        return cartRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException(userId));

                    Cart cart = Cart.builder()
                            .user(user)
                            .status("ACTIVE")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return cartRepository.save(cart);
                });
    }

    public Cart addItemToCart(Long userId, Long productId, Integer quantity) {

        Cart cart = getOrCreateActiveCart(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .addedAt(LocalDateTime.now())
                    .build();
        }

        cartItemRepository.save(cartItem);

        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
    public List<CartItem> getCartItems(Long userId) {

        Cart cart = cartRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() -> new ActiveCartNotFoundException(userId));

        return cartItemRepository.findByCartId(cart.getId());
    }
}