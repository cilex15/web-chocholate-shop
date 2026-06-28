package com.sergej.web_chocholate_shop.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex,
            Model model) {

        model.addAttribute("message",
                ex.getMessage());

        return "pages/error";
    }
}