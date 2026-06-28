package com.sergej.web_chocholate_shop.controller;

import com.sergej.web_chocholate_shop.dto.CartItemDTO;
import com.sergej.web_chocholate_shop.model.entity.Purchase;
import com.sergej.web_chocholate_shop.model.entity.User;
import com.sergej.web_chocholate_shop.model.enums.Role;
import com.sergej.web_chocholate_shop.service.PurchaseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;


    public PurchaseController(PurchaseService purchaseService) {

        this.purchaseService = purchaseService;
    }

    @PostMapping("/create")
    public String createPurchase(HttpSession session) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if(loggedUser == null) {

            return "redirect:/users/login";
        }

        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");

        if(cart == null || cart.isEmpty()) {

            return "redirect:/products";
        }

        purchaseService.createPurchase(loggedUser, cart);

        session.removeAttribute("cart");

        return "redirect:/profile";
    }

    @GetMapping
    public String getAllPurchases(
            HttpSession session,
            Model model) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if(loggedUser == null) {
            return "redirect:/users/login";
        }

        if(loggedUser.getRole().name().equals("CUSTOMER")) {
            return "redirect:/products";
        }

        model.addAttribute("purchases",
                purchaseService.findAll());

        return "pages/purchases";
    }

    @GetMapping("/cancel/{id}")
    public String showCancelPage(@PathVariable Long id,
            Model model) {

        model.addAttribute("purchase",
                purchaseService.findById(id));

        return "pages/cancel-purchase";
    }

    @PostMapping("/cancel/{id}")
    public String cancelPurchase(@PathVariable Long id,
            @RequestParam String reason) {

        purchaseService.cancelPurchase(id, reason);

        return "redirect:/profile";
    }

    @GetMapping("/{id}")
    public String purchaseDetails(@PathVariable Long id,
                                  HttpSession session,
                                  Model model) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/users/login";
        }

        model.addAttribute("purchase",
                purchaseService.findById(id));

        Purchase purchase = purchaseService.findById(id);

        if (!purchase.getUser().getId().equals(loggedUser.getId())
                && loggedUser.getRole() != Role.ADMIN) {

            throw new RuntimeException(
                    "You don't have permission to view this purchase.");
        }

        return "pages/purchase";
    }

}