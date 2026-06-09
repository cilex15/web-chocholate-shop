package com.sergej.web_chocholate_shop.service;

import com.sergej.web_chocholate_shop.model.entity.Discount;
import com.sergej.web_chocholate_shop.model.entity.Product;
import com.sergej.web_chocholate_shop.repository.DiscountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountService(
            DiscountRepository discountRepository) {

        this.discountRepository = discountRepository;
    }

    public List<Discount> findAll() {
        return discountRepository.findAll();
    }

    public Discount findById(Long id) {

        return discountRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Discount not found!"));
    }

    public Discount save(Discount discount) {

        if (discount.getDiscountPercent() < 0
                || discount.getDiscountPercent() > 100) {

            throw new RuntimeException(
                    "Discount must be between 0 and 100!"
            );
        }

        return discountRepository.save(discount);
    }

    public void deleteById(Long id) {

        findById(id);

        discountRepository.deleteById(id);
    }

    public boolean isDiscountActive(Discount discount) {

        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(discount.getStartDateTime())
                && now.isBefore(discount.getEndDateTime());
    }

    public boolean hasActiveDiscount(Product product) {

        if (product.getDiscount() == null) {
            return false;
        }

        return isDiscountActive(
                product.getDiscount()
        );
    }

    public BigDecimal calculateDiscountPrice(
            Product product) {

        Discount discount = product.getDiscount();

        if (discount == null) {
            return product.getPrice();
        }

        if (!isDiscountActive(discount)) {
            return product.getPrice();
        }

        BigDecimal discountValue =
                product.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        discount.getDiscountPercent()
                                ))
                        .divide(
                                BigDecimal.valueOf(100)
                        );

        return product.getPrice()
                .subtract(discountValue);
    }

}