package com.allan.shopping_cart_api.service;

import com.allan.shopping_cart_api.entity.Cart;
import com.allan.shopping_cart_api.entity.CartItem;
import com.allan.shopping_cart_api.entity.Order;
import com.allan.shopping_cart_api.entity.OrderItem;
import com.allan.shopping_cart_api.repository.CartItemRepository;
import com.allan.shopping_cart_api.repository.CartRepository;
import com.allan.shopping_cart_api.repository.OrderRepository;
import com.allan.shopping_cart_api.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public CheckoutService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order checkout(Long userId) {

        // Find the user's active cart.
        Cart cart = cartRepository
                .findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() ->
                        new RuntimeException("Active cart not found")
                );

        // Load all items currently in the cart.
        List<CartItem> cartItems =
                cartItemRepository.findByCartIdWithProduct(cart.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .status("CONFIRMED")
                .totalAmount(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {

            /*
             * Atomically checks available stock and deducts it.
             * If zero rows are updated, the requested quantity
             * is no longer available.
             */
            int updatedRows = productRepository.deductStock(
                    cartItem.getProduct().getId(),
                    cartItem.getQuantity()
            );

            if (updatedRows == 0) {
                throw new RuntimeException(
                        "Not enough stock for product: "
                                + cartItem.getProduct().getName()
                );
            }

            BigDecimal price = cartItem.getProduct().getPrice();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(price)
                    .build();

            order.getItems().add(orderItem);

            BigDecimal lineTotal = price.multiply(
                    BigDecimal.valueOf(cartItem.getQuantity())
            );

            totalAmount = totalAmount.add(lineTotal);
        }

        order.setTotalAmount(totalAmount);

        // Cascade saves all OrderItem records with the Order.
        Order savedOrder = orderRepository.save(order);

        // Mark this cart as completed so it cannot be reused.
        cart.setStatus("CHECKED_OUT");
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return savedOrder;
    }
}