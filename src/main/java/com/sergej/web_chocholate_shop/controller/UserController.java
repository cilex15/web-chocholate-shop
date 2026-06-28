package com.sergej.web_chocholate_shop.controller;

import com.sergej.web_chocholate_shop.dto.LoginDTO;
import com.sergej.web_chocholate_shop.model.entity.User;
import com.sergej.web_chocholate_shop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    @GetMapping
    public String getAllUsers(Model model,
            HttpSession session) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {

            return "redirect:/users/login";
        }

        model.addAttribute("users",
                userService.findAll());

        model.addAttribute("loggedUser",
                session.getAttribute("loggedUser"));

        return "pages/users";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute("user",
                new User()
        );

        return "pages/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
                               @RequestParam String confirmPassword) {

        if(!user.getPassword().equals(confirmPassword)) {

            throw new RuntimeException("Passwords do not match!");
        }

        userService.create(user);

        return "redirect:/users/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {

        model.addAttribute("loginDTO",
                new LoginDTO()
        );

        return "pages/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDTO loginDTO,
                        HttpSession session) {

        try {

            User user = userService.authenticate(loginDTO);

            session.setAttribute("loggedUser",
                    user);

            return "redirect:/products";

        }
        catch (RuntimeException e) {

            return "redirect:/users/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/users/login";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {

        userService.deleteById(id);

        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String editUserPage(@PathVariable Long id,
                               Model model) {

        model.addAttribute("user",
                userService.findById(id));

        return "pages/edit-user";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute User user) {

        userService.update(user);

        return "redirect:/users";
    }

}

