package com.sergej.web_chocholate_shop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addGlobalAttributes(Model model,
            HttpSession session) {

        model.addAttribute("loggedUser",
                session.getAttribute("loggedUser"));
    }
}