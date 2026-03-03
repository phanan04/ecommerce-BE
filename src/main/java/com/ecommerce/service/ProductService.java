package com.ecommerce.service;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Page<Product> getAllProducts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAll(pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Page<Product> searchProducts(String keyword, Long categoryId,
                                         BigDecimal minPrice, BigDecimal maxPrice,
                                         int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir != null && sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy != null ? sortBy : "createdAt").ascending()
                : Sort.by(sortBy != null ? sortBy : "createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.searchProducts(keyword, categoryId, minPrice, maxPrice, pageable);
    }

    public List<Product> getSuggestions(String keyword) {
        if (keyword == null || keyword.trim().length() < 2) return List.of();
        Pageable pageable = PageRequest.of(0, 8);
        return productRepository.findSuggestions(keyword.trim(), pageable);
    }

    public Page<Product> getByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    public Page<Product> getFeaturedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByFeaturedTrue(pageable);
    }

    public Page<Product> getNewArrivals(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByNewArrivalTrue(pageable);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setOriginalPrice(productDetails.getOriginalPrice());
        product.setImageUrl(productDetails.getImageUrl());
        product.setImages(productDetails.getImages());
        product.setCategory(productDetails.getCategory());
        product.setBrand(productDetails.getBrand());
        product.setSizes(productDetails.getSizes());
        product.setColors(productDetails.getColors());
        product.setStock(productDetails.getStock());
        product.setFeatured(productDetails.getFeatured());
        product.setNewArrival(productDetails.getNewArrival());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
}
