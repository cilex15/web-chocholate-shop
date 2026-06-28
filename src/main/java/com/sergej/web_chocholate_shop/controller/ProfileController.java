package com.sergej.web_chocholate_shop.controller;

import com.sergej.web_chocholate_shop.model.entity.User;
import com.sergej.web_chocholate_shop.service.PurchaseService;
import com.sergej.web_chocholate_shop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final PurchaseService purchaseService;

    private final UserService userService;

    public ProfileController(PurchaseService purchaseService, UserService userService) {
        this.purchaseService = purchaseService;
        this.userService = userService;
    }

    @GetMapping
    public String profilePage(HttpSession session,
                              Model model,
                              @RequestParam(required = false) LocalDate from,
                              @RequestParam(required = false) LocalDate to) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if(loggedUser == null) {

            return "redirect:/users/login";
        }

        model.addAttribute("user",
                loggedUser);

        if (from != null && to != null) {

            model.addAttribute("purchases",
                    purchaseService.findByUserBetweenDates(loggedUser, from, to));

        } else {

            model.addAttribute("purchases",
                    purchaseService.findByUser(loggedUser));
        }

        model.addAttribute("from", from);
        model.addAttribute("to", to);

        if(from != null && to != null && from.isAfter(to)) {

            throw new RuntimeException(
                    "Start date cannot be after end date.");
        }

        return "pages/profile";
    }

    @GetMapping("/edit")
    public String editProfilePage(HttpSession session,
            Model model) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if(loggedUser == null) {

            return "redirect:/users/login";
        }

        model.addAttribute("user",
                userService.findById(loggedUser.getId()));

        return "pages/edit-profile";
    }

    @PostMapping("/edit")
    public String editProfile(@ModelAttribute User user,
            HttpSession session) {

        User updatedUser = userService.updateProfile(user);

        session.setAttribute("loggedUser",
                updatedUser);

        return "redirect:/profile";
    }
}
