package com.sergej.web_chocholate_shop.service;

import com.sergej.web_chocholate_shop.model.enums.Role;
import org.springframework.stereotype.Service;
import com.sergej.web_chocholate_shop.model.entity.User;
import com.sergej.web_chocholate_shop.repository.UserRepository;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found!"));
    }

    public User findByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found!"));
    }

    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found!"));
    }

    public boolean existsByUsername(String username) {

        return userRepository.findByUsername(username).isPresent();
    }

    public User save(User user) {

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        user.setRegistrationDateTime(LocalDateTime.now());

        user.setRole(Role.CUSTOMER);

        return userRepository.save(user);
    }

    public void deleteById(Long id) {

        findById(id);

        userRepository.deleteById(id);
    }

}