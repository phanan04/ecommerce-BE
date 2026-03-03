package com.ecommerce.service;

import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = Cart.builder().user(user).build();
            return cartRepository.save(cart);
        });
    }

    @Transactional
    public Cart addToCart(String email, Long productId, String size, String color, int quantity) {
        Cart cart = getOrCreateCart(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId)
                        && item.getSize().equals(size)
                        && item.getColor().equals(color))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + quantity),
                        () -> cart.getItems().add(CartItem.builder()
                                .cart(cart)
                                .product(product)
                                .size(size)
                                .color(color)
                                .quantity(quantity)
                                .build())
                );

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateCartItem(String email, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(email);
        if (quantity <= 0) {
            cart.getItems().removeIf(item -> item.getId().equals(itemId));
        } else {
            cart.getItems().stream()
                    .filter(item -> item.getId().equals(itemId))
                    .findFirst()
                    .ifPresent(item -> item.setQuantity(quantity));
        }
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeFromCart(String email, Long itemId) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(String email) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
