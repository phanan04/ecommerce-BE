package com.ecommerce.repository;

import com.ecommerce.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserEmailOrderByCreatedAtDesc(String email);
    Optional<Wishlist> findByUserEmailAndProductId(String email, Long productId);
    boolean existsByUserEmailAndProductId(String email, Long productId);
    void deleteByUserEmailAndProductId(String email, Long productId);
}
