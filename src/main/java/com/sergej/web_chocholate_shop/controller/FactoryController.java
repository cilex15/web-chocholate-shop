package com.sergej.web_chocholate_shop.controller;

import com.sergej.web_chocholate_shop.model.entity.Factory;
import com.sergej.web_chocholate_shop.service.FactoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/factories")
public class FactoryController {

    private final FactoryService factoryService;

    public FactoryController(FactoryService factoryService) {

        this.factoryService = factoryService;
    }

    @GetMapping
    public String getAllFactories(
            Model model) {

        model.addAttribute("factories",
                factoryService.findAll());

        return "pages/factories";
    }

    @GetMapping("/create")
    public String createFactoryPage(
            Model model) {

        model.addAttribute("factory",
                new Factory());

        return "pages/create-factory";
    }

    @PostMapping("/create")
    public String createFactory(@ModelAttribute Factory factory) {

        factoryService.create(factory);

        return "redirect:/factories";
    }

    @GetMapping("/edit/{id}")
    public String editFactoryPage(@PathVariable Long id,
                                  Model model) {

        model.addAttribute("factory",
                factoryService.findById(id));

        return "pages/edit-factory";
    }

    @PostMapping("/edit")
    public String editFactory(@ModelAttribute Factory factory) {

        factoryService.update(factory);

        return "redirect:/factories";
    }

    @GetMapping("/delete/{id}")
    public String deleteFactory(@PathVariable Long id) {

        factoryService.deleteById(id);

        return "redirect:/factories";
    }

}