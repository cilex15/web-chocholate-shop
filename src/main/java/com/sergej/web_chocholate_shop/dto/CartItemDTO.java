package com.sergej.web_chocholate_shop.dto;

import java.math.BigDecimal;

public class CartItemDTO {

    private Long productId;

    private String productName;

    private BigDecimal price;

    private Integer quantity;

    public CartItemDTO() {
    }

    public CartItemDTO(
            Long productId,
            String productName,
            BigDecimal price,
            Integer quantity) {

        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {

        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
