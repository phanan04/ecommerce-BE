package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.model.Wishlist;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<Wishlist> getWishlist(String email) {
        return wishlistRepository.findByUserEmailOrderByCreatedAtDesc(email);
    }

    @Transactional
    public Map<String, Object> toggleWishlist(String email, Long productId) {
        if (wishlistRepository.existsByUserEmailAndProductId(email, productId)) {
            wishlistRepository.deleteByUserEmailAndProductId(email, productId);
            return Map.of("action", "removed", "productId", productId);
        } else {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            Wishlist wishlist = Wishlist.builder().user(user).product(product).build();
            wishlistRepository.save(wishlist);
            return Map.of("action", "added", "productId", productId);
        }
    }

    public boolean isWishlisted(String email, Long productId) {
        return wishlistRepository.existsByUserEmailAndProductId(email, productId);
    }

    @Transactional
    public void removeFromWishlist(String email, Long productId) {
        wishlistRepository.deleteByUserEmailAndProductId(email, productId);
    }
}
