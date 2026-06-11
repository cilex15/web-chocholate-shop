package com.sergej.web_chocholate_shop.service;

import com.sergej.web_chocholate_shop.dto.CartItemDTO;
import com.sergej.web_chocholate_shop.model.entity.Purchase;
import com.sergej.web_chocholate_shop.model.entity.Product;
import com.sergej.web_chocholate_shop.model.entity.PurchaseItem;
import com.sergej.web_chocholate_shop.model.entity.User;
import com.sergej.web_chocholate_shop.model.enums.PurchaseStatus;
import com.sergej.web_chocholate_shop.repository.PurchaseItemRepository;
import com.sergej.web_chocholate_shop.repository.PurchaseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    private final ProductService productService;

    private final DiscountService discountService;

    private final PurchaseItemRepository purchaseItemRepository;

    public PurchaseService(
            PurchaseRepository purchaseRepository,
            ProductService productService,
            DiscountService discountService, PurchaseItemRepository purchaseItemRepository) {

        this.purchaseRepository = purchaseRepository;
        this.productService = productService;
        this.discountService = discountService;
        this.purchaseItemRepository = purchaseItemRepository;
    }

    public List<Purchase> findAll() {
        return purchaseRepository.findAll();
    }

    public Purchase findById(Long id) {

        return purchaseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Purchase not found!"
                        ));
    }

    public Purchase save(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    public void deleteById(Long id) {

        findById(id);

        purchaseRepository.deleteById(id);
    }

    public BigDecimal calculateTotalPrice(
            List<CartItemDTO> cart) {

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItemDTO cartItem : cart) {

            Product product =
                    productService.findById(
                            cartItem.getProductId()
                    );

            BigDecimal currentPrice =
                    discountService
                            .calculateDiscountPrice(
                                    product
                            );

            BigDecimal itemTotal =
                    currentPrice.multiply(
                            BigDecimal.valueOf(
                                    cartItem.getQuantity()
                            )
                    );

            totalPrice =
                    totalPrice.add(itemTotal);
        }

        return totalPrice;
    }

    public void decreaseStock(
            List<CartItemDTO> cart) {

        for (CartItemDTO cartItem : cart) {

            Product product =
                    productService.findById(
                            cartItem.getProductId()
                    );

            if (product.getStockQuantity()
                    < cartItem.getQuantity()) {

                throw new RuntimeException(
                        "Not enough products in stock!"
                );
            }

            product.setStockQuantity(
                    product.getStockQuantity()
                            - cartItem.getQuantity()
            );

            productService.update(product);
        }
    }

    public Purchase createPurchase(User user, List<CartItemDTO> cart) {

        if (cart == null
                || cart.isEmpty()) {

            throw new RuntimeException(
                    "Cart is empty!"
            );
        }

        BigDecimal totalPrice =
                calculateTotalPrice(
                        cart
                );

        Purchase purchase =
                new Purchase();

        purchase.setUser(user);

        purchase.setPurchaseDateTime(
                LocalDateTime.now()
        );

        purchase.setStatus(
                PurchaseStatus.PURCHASED
        );

        purchase.setTotalPrice(
                totalPrice
        );

        List<PurchaseItem> items =
                new ArrayList<>();

        for (CartItemDTO cartItem
                : cart) {

            Product product =
                    productService.findById(
                            cartItem.getProductId()
                    );

            PurchaseItem item =
                    new PurchaseItem();

            item.setPurchase(
                    purchase
            );

            item.setProduct(
                    product
            );

            item.setQuantity(
                    cartItem.getQuantity()
            );

            item.setPriceAtPurchase(
                    discountService
                            .calculateDiscountPrice(
                                    product
                            )
            );

            items.add(item);
        }

        purchase.setItems(
                items
        );

        decreaseStock(
                cart
        );

        return purchaseRepository
                .save(
                        purchase
                );
    }

    public Purchase cancelPurchase(
            Long purchaseId,
            String reason) {

        Purchase purchase =
                findById(purchaseId);

        if (purchase.getStatus()
                == PurchaseStatus.CANCELLED) {

            throw new RuntimeException(
                    "Purchase already cancelled!"
            );
        }

        if (LocalDateTime.now().isAfter(

                purchase
                        .getPurchaseDateTime()
                        .plusHours(1)

        )) {

            throw new RuntimeException(
                    "Cancellation period expired!"
            );
        }

        purchase.setStatus(
                PurchaseStatus.CANCELLED
        );

        purchase.setCancellationReason(
                reason
        );

        purchase.setCancelledAt(
                LocalDateTime.now()
        );

        increaseStock(purchase);

        return purchaseRepository
                .save(purchase);
    }

    public void increaseStock(
            Purchase purchase) {

        for (PurchaseItem item
                : purchase.getItems()) {

            Product product =
                    item.getProduct();

            product.setStockQuantity(

                    product.getStockQuantity()

                            + item.getQuantity()

            );

            productService.update(
                    product
            );
        }
    }

}