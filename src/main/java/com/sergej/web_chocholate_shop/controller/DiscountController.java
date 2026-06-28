package com.sergej.web_chocholate_shop.controller;

import com.sergej.web_chocholate_shop.model.entity.Discount;
import com.sergej.web_chocholate_shop.service.DiscountService;
import com.sergej.web_chocholate_shop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/discounts")
public class DiscountController {

    private final DiscountService discountService;

    private final ProductService productService;

    public DiscountController(
            DiscountService discountService,
            ProductService productService) {

        this.discountService = discountService;
        this.productService = productService;
    }

    @GetMapping
    public String getAllDiscounts(Model model) {

        model.addAttribute("discounts",
                discountService.findAll());

        return "pages/discounts";
    }

    @GetMapping("/create")
    public String createDiscountPage(
            Model model) {

        model.addAttribute("discount",
                new Discount());

        model.addAttribute("products",
                productService.findAll());

        return "pages/create-discount";
    }

    @PostMapping("/create")
    public String createDiscount(
            @ModelAttribute Discount discount) {

        discountService.create(discount);

        return "redirect:/discounts";
    }

    @GetMapping("/edit/{id}")
    public String editDiscountPage(@PathVariable Long id,
            Model model) {

        model.addAttribute("discount",
                discountService.findById(id));

        model.addAttribute("products",
                productService.findAll());

        return "pages/edit-discount";
    }

    @PostMapping("/edit")
    public String editDiscount(@ModelAttribute Discount discount) {

        discountService.update(discount);

        return "redirect:/discounts";
    }

    @GetMapping("/delete/{id}")
    public String deleteDiscount(@PathVariable Long id) {

        discountService.deleteById(id);

        return "redirect:/discounts";
    }

}