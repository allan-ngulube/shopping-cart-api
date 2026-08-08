package com.allan.shopping_cart_api.service;

import com.allan.shopping_cart_api.entity.Cart;
import com.allan.shopping_cart_api.entity.CartItem;
import com.allan.shopping_cart_api.entity.Order;
import com.allan.shopping_cart_api.entity.OrderItem;
import com.allan.shopping_cart_api.kafka.event.CheckoutCompletedEvent;
import com.allan.shopping_cart_api.kafka.producer.CheckoutEventProducer;
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
    private final CheckoutEventProducer checkoutEventProducer;

    public CheckoutService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            CheckoutEventProducer checkoutEventProducer
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.checkoutEventProducer = checkoutEventProducer;
    }

    @Transactional
    public Order checkout(Long userId) {

        Cart cart = cartRepository
                .findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() ->
                        new RuntimeException("Active cart not found")
                );

        /*
         * Read the user information before the bulk stock update.
         * The stock update may clear or detach entities from Hibernate's
         * persistence context, which can make the lazy User proxy unavailable.
         */
        int customerId = cart.getUser().getId();
        String customerEmail = cart.getUser().getEmail();

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

        Order savedOrder = orderRepository.save(order);

        cart.setStatus("CHECKED_OUT");
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        CheckoutCompletedEvent event = new CheckoutCompletedEvent(
                savedOrder.getId(),
                customerId,
                customerEmail,
                savedOrder.getTotalAmount(),
                savedOrder.getStatus()
        );

        checkoutEventProducer.publish(event);

        return savedOrder;
    }
}