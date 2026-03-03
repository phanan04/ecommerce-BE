package com.ecommerce.config;

import com.ecommerce.model.Category;
import com.ecommerce.model.Coupon;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.CouponRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Always seed coupons if none exist (independent of user seeding)
        if (couponRepository.count() == 0) {
            couponRepository.saveAll(List.of(
                Coupon.builder()
                    .code("NIKE10")
                    .discountType(Coupon.DiscountType.PERCENT)
                    .discountValue(new BigDecimal("10"))
                    .minOrderAmount(new BigDecimal("50"))
                    .maxUses(100)
                    .usedCount(0)
                    .expiresAt(LocalDateTime.now().plusYears(1))
                    .active(true)
                    .build(),
                Coupon.builder()
                    .code("SAVE20")
                    .discountType(Coupon.DiscountType.FIXED)
                    .discountValue(new BigDecimal("20"))
                    .minOrderAmount(new BigDecimal("100"))
                    .maxUses(50)
                    .usedCount(0)
                    .expiresAt(LocalDateTime.now().plusYears(1))
                    .active(true)
                    .build(),
                Coupon.builder()
                    .code("WELCOME15")
                    .discountType(Coupon.DiscountType.PERCENT)
                    .discountValue(new BigDecimal("15"))
                    .minOrderAmount(null)
                    .maxUses(null)
                    .usedCount(0)
                    .expiresAt(LocalDateTime.now().plusYears(1))
                    .active(true)
                    .build()
            ));
            log.info("Seeded 3 sample coupons: NIKE10, SAVE20, WELCOME15");
        }

        if (userRepository.count() > 0) return;

        log.info("Seeding initial data...");

        // Admin user
        userRepository.save(User.builder()
                .email("admin@nike.com")
                .password(passwordEncoder.encode("admin123"))
                .firstName("Admin")
                .lastName("Nike")
                .role(User.Role.ADMIN)
                .build());

        // Demo user
        userRepository.save(User.builder()
                .email("user@nike.com")
                .password(passwordEncoder.encode("user123"))
                .firstName("John")
                .lastName("Doe")
                .role(User.Role.USER)
                .build());

        // Categories
        Category shoes = categoryRepository.save(Category.builder()
                .name("Shoes").slug("shoes").description("Nike footwear").build());
        Category clothing = categoryRepository.save(Category.builder()
                .name("Clothing").slug("clothing").description("Nike apparel").build());
        Category accessories = categoryRepository.save(Category.builder()
                .name("Accessories").slug("accessories").description("Nike gear & accessories").build());
        Category running = categoryRepository.save(Category.builder()
                .name("Running").slug("running").description("Running gear").build());

        // Products
        List<Product> products = List.of(
            Product.builder()
                .name("Nike Air Max 270")
                .description("The Nike Air Max 270 delivers visible cushioning under every step. The design draws inspiration from Air Max icons, showcasing Nike's biggest heel Air unit yet.")
                .price(new BigDecimal("150.00"))
                .originalPrice(new BigDecimal("180.00"))
                .imageUrl("https://static.nike.com/a/images/f_auto/dpr_2.0,cs_srgb/w_600,c_limit/0da17dda-c87a-4f31-a7c2-3eed00af8ad9/image.png")
                .brand("Nike")
                .category(shoes)
                .sizes(List.of("7", "8", "9", "10", "11", "12"))
                .colors(List.of("Black/White", "White/Red", "Blue/White"))
                .stock(50)
                .featured(true)
                .newArrival(false)
                .rating(4.8)
                .reviewCount(2340)
                .build(),

            Product.builder()
                .name("Nike Air Force 1 '07")
                .description("The radically original Nike Air Force 1 took basketball by storm in 1982 before becoming a street icon. The hoops heritage lives on in this re-released classic.")
                .price(new BigDecimal("110.00"))
                .originalPrice(null)
                .imageUrl("https://static.nike.com/a/images/f_auto/dpr_2.0,cs_srgb/w_600,c_limit/9a917553-5eac-453f-b0c0-7b09f8e0e0b0/image.png")
                .brand("Nike")
                .category(shoes)
                .sizes(List.of("7", "8", "9", "10", "11", "12"))
                .colors(List.of("White", "Black", "White/University Red"))
                .stock(80)
                .featured(true)
                .newArrival(true)
                .rating(4.9)
                .reviewCount(5621)
                .build(),

            Product.builder()
                .name("Nike Pegasus 41")
                .description("The Nike Pegasus 41 continues the legacy of one of Nike's most beloved running shoes with ReactX foam providing 13% more energy return than the previous generation.")
                .price(new BigDecimal("130.00"))
                .originalPrice(null)
                .imageUrl("https://static.nike.com/a/images/f_auto/dpr_2.0,cs_srgb/w_600,c_limit/e0d32e95-1253-4553-8fe0-7ce6d76aecf9/image.png")
                .brand("Nike")
                .category(running)
                .sizes(List.of("7", "7.5", "8", "8.5", "9", "9.5", "10", "10.5", "11"))
                .colors(List.of("Gym Red/White", "Black/White", "Blue/Silver"))
                .stock(60)
                .featured(true)
                .newArrival(true)
                .rating(4.7)
                .reviewCount(1890)
                .build(),

            Product.builder()
                .name("Nike Dri-FIT ADV TechKnit Ultra T-Shirt")
                .description("Engineered for elite runners, the Nike Dri-FIT ADV TechKnit Ultra T-Shirt uses strategic ventilation zones to keep you cool mile after mile.")
                .price(new BigDecimal("90.00"))
                .originalPrice(new BigDecimal("110.00"))
                .imageUrl("https://static.nike.com/a/images/f_auto/dpr_2.0,cs_srgb/w_600,c_limit/7657a90f-5003-43cb-a7f0-bdc50a1e0cb8/image.png")
                .brand("Nike")
                .category(clothing)
                .sizes(List.of("XS", "S", "M", "L", "XL", "2XL"))
                .colors(List.of("Black", "White", "Blue Void"))
                .stock(120)
                .featured(false)
                .newArrival(true)
                .rating(4.6)
                .reviewCount(430)
                .build(),

            Product.builder()
                .name("Nike Sportswear Tech Fleece Joggers")
                .description("Made from Nike Tech Fleece material, these joggers balance warmth with a lightweight feel — perfect for cool days on or off the field.")
                .price(new BigDecimal("100.00"))
                .originalPrice(null)
                .imageUrl("https://static.nike.com/a/images/f_auto/dpr_2.0,cs_srgb/w_600,c_limit/b0b5a65b-78c2-4f44-8bec-3b32c8d22e5b/image.png")
                .brand("Nike")
                .category(clothing)
                .sizes(List.of("XS", "S", "M", "L", "XL", "2XL"))
                .colors(List.of("Carbon Heather/Black", "Dark Grey Heather/Black"))
                .stock(75)
                .featured(false)
                .newArrival(false)
                .rating(4.5)
                .reviewCount(890)
                .build(),

            Product.builder()
                .name("Nike Phantom GX 2 Elite FG")
                .description("Designed for the most precise players, the Nike Phantom GX 2 Elite features a textured front foot for ball control and a Ghost lacing system for a clean strike zone.")
                .price(new BigDecimal("275.00"))
                .originalPrice(null)
                .imageUrl("https://static.nike.com/a/images/f_auto/dpr_2.0,cs_srgb/w_600,c_limit/ba95e5b1-9c31-493e-a1c7-fa2e07a027fe/image.png")
                .brand("Nike")
                .category(shoes)
                .sizes(List.of("7", "7.5", "8", "8.5", "9", "9.5", "10", "10.5", "11", "11.5", "12"))
                .colors(List.of("Black/Chrome", "Bright Crimson/Black"))
                .stock(30)
                .featured(true)
                .newArrival(false)
                .rating(4.8)
                .reviewCount(312)
                .build(),

            Product.builder()
                .name("Nike Fuel Pack 2.0 Running Belt")
                .description("Carry your essentials hands-free with the Nike Fuel Pack 2.0. Designed for runners, it features quick-access pockets and an adjustable fit.")
                .price(new BigDecimal("35.00"))
                .originalPrice(new BigDecimal("45.00"))
                .imageUrl("https://static.nike.com/a/images/f_auto/dpr_2.0,cs_srgb/w_600,c_limit/44f35d77-c7c1-4a28-a869-7e74e2d3b05f/image.png")
                .brand("Nike")
                .category(accessories)
                .sizes(List.of("One Size"))
                .colors(List.of("Black", "White"))
                .stock(200)
                .featured(false)
                .newArrival(false)
                .rating(4.4)
                .reviewCount(157)
                .build(),

            Product.builder()
                .name("Nike Heritage Backpack")
                .description("The Nike Heritage Backpack keeps your gear organized with multiple pockets and adjustable shoulder straps. Its durable design can handle everyday adventures.")
                .price(new BigDecimal("55.00"))
                .originalPrice(null)
                .imageUrl("https://static.nike.com/a/images/f_auto/dpr_2.0,cs_srgb/w_600,c_limit/07cfc78a-6a4f-41a8-9826-de84d3e58c0a/image.png")
                .brand("Nike")
                .category(accessories)
                .sizes(List.of("One Size"))
                .colors(List.of("Black", "Navy", "Grey"))
                .stock(150)
                .featured(false)
                .newArrival(true)
                .rating(4.5)
                .reviewCount(3401)
                .build()
        );

        productRepository.saveAll(products);

        log.info("Seed complete: {} products, {} categories, 2 users", products.size(), 4);
    }
}