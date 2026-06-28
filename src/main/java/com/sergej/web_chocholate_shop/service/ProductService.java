package com.sergej.web_chocholate_shop.service;

import com.sergej.web_chocholate_shop.model.entity.Discount;
import com.sergej.web_chocholate_shop.model.entity.Factory;
import com.sergej.web_chocholate_shop.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.sergej.web_chocholate_shop.model.entity.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final FactoryService factoryService;

    public ProductService(ProductRepository productRepository, FactoryService factoryService) {
        this.productRepository = productRepository;
        this.factoryService = factoryService;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found!"));
    }

    public Product findByCode(String code) {

        return productRepository.findByCode(code)
                .orElseThrow(() ->
                        new RuntimeException("Product not found!"));
    }

    public List<Product> searchByName(String name) {

        return productRepository.findByNameContaining(name);
    }

    public boolean existsByCode(String code) {

        return productRepository.findByCode(code).isPresent();
    }

    public Product create(Product product) {

        Factory factory = factoryService.findById(
                product.getFactory().getId());

        product.setFactory(factory);

        if (existsByCode(product.getCode())) {
            throw new RuntimeException(
                    "Product code already exists!"
            );
        }

        if (product.getFactory() == null) {

            throw new RuntimeException(
                    "Factory must be selected!"
            );
        }

        if (product.getPrice().signum() < 0) {
            throw new RuntimeException(
                    "Price cannot be negative!"
            );
        }

        if (product.getStockQuantity() < 0) {
            throw new RuntimeException(
                    "Stock quantity cannot be negative!"
            );
        }

        return productRepository.save(product);
    }

    public Product update(Product product) {

        findById(product.getId());

        Factory factory = factoryService.findById(
                        product.getFactory().getId());

        product.setFactory(factory);

        if (product.getPrice().signum() < 0) {
            throw new RuntimeException(
                    "Price cannot be negative!"
            );
        }

        if (product.getStockQuantity() < 0) {
            throw new RuntimeException(
                    "Stock quantity cannot be negative!"
            );
        }

        return productRepository.save(product);
    }

    public void deleteById(Long id) {

        findById(id);

        Product product = findById(id);

        if(!product.getPurchaseItems().isEmpty()) {

            throw new RuntimeException("Product cannot be deleted because it exists in purchases.");
        }

        productRepository.deleteById(id);
    }

    public BigDecimal calculateCurrentPrice(
            Product product) {

        Discount discount = product.getDiscount();

        if(discount == null) {

            return product.getPrice();
        }

        LocalDateTime now = LocalDateTime.now();

        if(now.isBefore(discount.getStartDateTime())) {

            return product.getPrice();
        }

        if(now.isAfter(discount.getEndDateTime())
        ) {

            return product.getPrice();
        }

        BigDecimal percent = BigDecimal.valueOf(
                        discount.getDiscountPercent());

        BigDecimal multiplier =
                BigDecimal.valueOf(100)
                        .subtract(percent)
                        .divide(BigDecimal.valueOf(100));

        return product.getPrice()
                .multiply(multiplier).setScale(2,
                        RoundingMode.HALF_UP);
    }

    public boolean hasActiveDiscount(
            Product product) {

        Discount discount = product.getDiscount();

        if(discount == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        return !now.isBefore(
                discount.getStartDateTime())
                &&
                !now.isAfter(
                        discount.getEndDateTime());
    }

    public List<Product> searchProducts(
            String code,
            String name,
            Long factoryId,
            Integer minStock,
            Integer maxStock,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sortBy,
            String direction) {

        Sort sort = Sort.unsorted();

        if (sortBy != null && !sortBy.isBlank()) {

            if ("desc".equalsIgnoreCase(direction)) {

                sort = Sort.by(sortBy).descending();

            } else {

                sort = Sort.by(sortBy).ascending();

            }
        }

        return productRepository.searchProducts(
                code,
                name,
                factoryId,
                minStock,
                maxStock,
                minPrice,
                maxPrice,
                sort);
    }






}
