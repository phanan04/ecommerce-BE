package com.ecommerce.controller;

import com.ecommerce.model.Coupon;
import com.ecommerce.service.CouponService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // Validate coupon (authenticated users)
    @PostMapping("/api/coupons/validate")
    public ResponseEntity<Map<String, Object>> validateCoupon(@RequestBody ValidateRequest request) {
        return ResponseEntity.ok(couponService.validateCoupon(request.getCode(), request.getOrderAmount()));
    }

    // Admin endpoints
    @GetMapping("/api/admin/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @PostMapping("/api/admin/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.createCoupon(coupon));
    }

    @DeleteMapping("/api/admin/coupons/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @Data
    static class ValidateRequest {
        private String code;
        private BigDecimal orderAmount;
    }
}
