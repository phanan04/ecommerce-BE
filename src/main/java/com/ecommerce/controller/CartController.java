package com.ecommerce.controller;

import com.ecommerce.model.Cart;
import com.ecommerce.service.CartService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getOrCreateCart(userDetails.getUsername()));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(
                userDetails.getUsername(),
                request.getProductId(),
                request.getSize(),
                request.getColor(),
                request.getQuantity()
        ));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<Cart> updateItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId,
            @RequestBody UpdateCartRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(
                userDetails.getUsername(), itemId, request.getQuantity()));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Cart> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeFromCart(userDetails.getUsername(), itemId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Data
    static class AddToCartRequest {
        private Long productId;
        private String size;
        private String color;
        private int quantity = 1;
    }

    @Data
    static class UpdateCartRequest {
        private int quantity;
    }
}
