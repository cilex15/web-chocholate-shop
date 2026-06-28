package com.sergej.web_chocholate_shop.controller;

import com.sergej.web_chocholate_shop.dto.CartItemDTO;
import com.sergej.web_chocholate_shop.model.entity.Product;
import com.sergej.web_chocholate_shop.model.entity.User;
import com.sergej.web_chocholate_shop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductService productService;

    public CartController(ProductService productService) {

        this.productService = productService;
    }



    @GetMapping
    public String cartPage(HttpSession session,
            Model model) {

        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");

        if(cart == null) {

            cart = new ArrayList<>();
        }

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {

            return "redirect:/users/login";
        }

        model.addAttribute("cart",
                cart);

        model.addAttribute("cartTotal",
                calculateCartTotal(cart)
        );

        return "pages/cart";
    }


    @PostMapping("/add/{id}")
    public String addToCart(
            @PathVariable Long id,
            @RequestParam int quantity,
            HttpSession session) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {

            return "redirect:/users/login";
        }

        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");

        if (cart == null) {

            cart = new ArrayList<>();
        }

        Product product =
                productService.findById(id);

        if (quantity < 1) {

            throw new RuntimeException("Quantity must be greater than zero.");
        }

        if (quantity > product.getStockQuantity()) {

            throw new RuntimeException("Requested quantity exceeds available stock.");
        }

        boolean exists = false;

        for (CartItemDTO item : cart) {

            if (item.getProductId().equals(id)) {

                int newQuantity =
                        item.getQuantity() + quantity;

                if (newQuantity > product.getStockQuantity()) {

                    throw new RuntimeException(
                            "Requested quantity exceeds available stock.");
                }

                item.setQuantity(newQuantity);

                exists = true;

                break;
            }
        }

        if (!exists) {

            CartItemDTO item =
                    new CartItemDTO(
                            product.getId(),
                            product.getName(),
                            productService.calculateCurrentPrice(product),
                            quantity);

            cart.add(item);
        }

        session.setAttribute("cart", cart);

        return "redirect:/products";
    }



    @GetMapping("/remove/{id}")
    public String removeFromCart(
            @PathVariable Long id,
            HttpSession session) {

        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");

        if(cart != null) {

            cart.removeIf(item ->
                            item.getProductId()
                                    .equals(id));

            session.setAttribute("cart",
                    cart);
        }

        return "redirect:/cart";
    }

    private BigDecimal calculateCartTotal(
            List<CartItemDTO> cart) {

        BigDecimal total = BigDecimal.ZERO;

        for(CartItemDTO item : cart) {

            total = total.add(item.getTotalPrice());
        }

        return total;
    }
}
