package com.sergej.web_chocholate_shop.service;

import com.sergej.web_chocholate_shop.model.entity.Factory;
import com.sergej.web_chocholate_shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import com.sergej.web_chocholate_shop.model.entity.Product;

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

        productRepository.deleteById(id);
    }
}
