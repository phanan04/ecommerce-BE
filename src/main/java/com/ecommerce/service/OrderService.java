package com.ecommerce.service;

import com.ecommerce.model.*;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final CouponService couponService;

    @Transactional
    public Order createOrderFromCart(String email, String shippingAddress, String paymentMethod) {
        return createOrderFromCart(email, shippingAddress, paymentMethod, null);
    }

    @Transactional
    public Order createOrderFromCart(String email, String shippingAddress, String paymentMethod, String couponCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Cart cart = cartService.getOrCreateCart(email);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .size(cartItem.getSize())
                        .color(cartItem.getColor())
                        .price(cartItem.getProduct().getPrice())
                        .build())
                .collect(Collectors.toList());

        BigDecimal total = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Apply coupon discount
        BigDecimal discount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.isBlank()) {
            try {
                discount = couponService.applyAndGetDiscount(couponCode, total);
            } catch (Exception ignored) {
                // Invalid coupon — proceed without discount
            }
        }
        BigDecimal finalTotal = total.subtract(discount).max(BigDecimal.ZERO);

        Order order = Order.builder()
                .user(user)
                .items(orderItems)
                .total(finalTotal)
                .shippingAddress(shippingAddress)
                .paymentMethod(paymentMethod)
                .status(Order.OrderStatus.PENDING)
                .build();

        orderItems.forEach(item -> item.setOrder(order));
        Order saved = orderRepository.save(order);
        cartService.clearCart(email);
        return saved;
    }

    public List<Order> getMyOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
