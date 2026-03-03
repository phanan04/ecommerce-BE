package com.ecommerce.controller;

import com.ecommerce.model.Wishlist;
import com.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<Wishlist>> getWishlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(wishlistService.getWishlist(userDetails.getUsername()));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> toggleWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.toggleWishlist(userDetails.getUsername(), productId));
    }

    @GetMapping("/{productId}/status")
    public ResponseEntity<Map<String, Boolean>> checkWishlistStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        boolean wishlisted = wishlistService.isWishlisted(userDetails.getUsername(), productId);
        return ResponseEntity.ok(Map.of("wishlisted", wishlisted));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        wishlistService.removeFromWishlist(userDetails.getUsername(), productId);
        return ResponseEntity.noContent().build();
    }
}
