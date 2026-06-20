package com.sergej.web_chocholate_shop.controller;

import com.sergej.web_chocholate_shop.model.entity.Product;
import com.sergej.web_chocholate_shop.model.entity.User;
import com.sergej.web_chocholate_shop.service.FactoryService;
import com.sergej.web_chocholate_shop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    private final FactoryService factoryService;

    public ProductController(
            ProductService productService,
            FactoryService factoryService) {

        this.productService = productService;
        this.factoryService = factoryService;
    }

    @GetMapping
    public String getAllProducts(
            Model model) {


        model.addAttribute("products",
                productService.findAll());


        return "pages/products";
    }

    @GetMapping("/create")
    public String createProductPage(
            Model model) {

        model.addAttribute("product",
                new Product());

        model.addAttribute("factories",
                factoryService.findAll());

        return "pages/create-product";
    }

    @PostMapping("/create")
    public String createProduct(@ModelAttribute Product product) {

        productService.create(product);

        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String editProductPage(@PathVariable Long id,
            Model model) {

        model.addAttribute("product",
                productService.findById(id));

        model.addAttribute("factories",
                factoryService.findAll());

        return "pages/edit-product";
    }

    @PostMapping("/edit")
    public String editProduct(@ModelAttribute Product product) {

        productService.update(product);

        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id) {

        productService.deleteById(id);

        return "redirect:/products";
    }

}