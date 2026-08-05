package com.allan.shopping_cart_api;

import com.allan.shopping_cart_api.entity.*;
import com.allan.shopping_cart_api.repository.*;
import com.allan.shopping_cart_api.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CartServiceTest {

    @Mock
    CartRepository cartRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CartItemRepository cartItemRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    CartService cartService;

    @Test
    void getOrCreateActiveCart_returnsExistingCart() {

        Cart cart = Cart.builder()
                .id(1L)
                .status("ACTIVE")
                .build();

        when(cartRepository.findByUserIdAndStatus(1L, "ACTIVE"))
                .thenReturn(Optional.of(cart));

        Cart result = cartService.getOrCreateActiveCart(1L);

        assertEquals(1L, result.getId());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getOrCreateActiveCart_createsCartWhenNoneExists() {

        User user = new User();
        user.setId(1);

        when(cartRepository.findByUserIdAndStatus(1L, "ACTIVE"))
                .thenReturn(Optional.empty());

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Cart result = cartService.getOrCreateActiveCart(1L);

        assertEquals("ACTIVE", result.getStatus());
        assertEquals(user, result.getUser());

        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addItemToCart_addsNewItem() {

        User user = new User();
        user.setId(1);

        Cart cart = Cart.builder()
                .id(10L)
                .user(user)
                .status("ACTIVE")
                .build();

        Product product = new Product();
        product.setId(5L);
        product.setName("MacBook");
        product.setStockQuantity(10);

        when(cartRepository.findByUserIdAndStatus(1L, "ACTIVE"))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(5L))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartIdAndProductId(10L, 5L))
                .thenReturn(Optional.empty());

        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Cart result = cartService.addItemToCart(1L, 5L, 2);

        assertEquals(10L, result.getId());

        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).save(cart);
    }

    @Test
    void addItemToCart_increasesQuantityWhenItemExists() {

        Cart cart = Cart.builder()
                .id(10L)
                .status("ACTIVE")
                .build();

        Product product = new Product();
        product.setId(5L);
        product.setName("MacBook");
        product.setStockQuantity(10);

        CartItem existingItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        when(cartRepository.findByUserIdAndStatus(1L, "ACTIVE"))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(5L))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartIdAndProductId(10L, 5L))
                .thenReturn(Optional.of(existingItem));

        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        cartService.addItemToCart(1L, 5L, 3);

        assertEquals(5, existingItem.getQuantity());

        verify(cartItemRepository).save(existingItem);
    }

    @Test
    void addItemToCart_throwsWhenRequestedQuantityExceedsStock() {

        Cart cart = Cart.builder()
                .id(10L)
                .status("ACTIVE")
                .build();

        Product product = new Product();
        product.setId(5L);
        product.setStockQuantity(2);

        when(cartRepository.findByUserIdAndStatus(1L, "ACTIVE"))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(5L))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartIdAndProductId(10L, 5L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cartService.addItemToCart(1L, 5L, 3)
        );

        assertEquals(
                "Not enough inventory available",
                exception.getMessage()
        );

        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void getCartItems_returnsItemsForActiveCart() {

        Cart cart = Cart.builder()
                .id(10L)
                .status("ACTIVE")
                .build();

        CartItem item = CartItem.builder()
                .quantity(2)
                .build();

        when(cartRepository.findByUserIdAndStatus(1L, "ACTIVE"))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCartId(10L))
                .thenReturn(List.of(item));

        List<CartItem> result = cartService.getCartItems(1L);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getQuantity());
    }
}