package com.sergej.web_chocholate_shop.service;

import com.sergej.web_chocholate_shop.dto.LoginDTO;
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

    public User create(User user) {

        validate(user);

        user.setRegistrationDateTime(LocalDateTime.now());

        user.setRole(Role.CUSTOMER);

        return userRepository.save(user);
    }

    public User update(User user) {

        validateForUpdate(user);

        findById(user.getId());

        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }


        return userRepository.save(user);
    }

    public void deleteById(Long id) {

        findById(id);

        userRepository.deleteById(id);
    }

    public User authenticate(
            LoginDTO loginDTO) {

        User user =
                findByUsername(
                        loginDTO.getUsername()
                );

        if (!user.getPassword().equals(
                loginDTO.getPassword()
        )) {

            throw new RuntimeException(
                    "Invalid username or password!"
            );
        }

        return user;
    }

    private void validate(User user) {

        if(user.getUsername() == null || user.getUsername().isBlank()) {

            throw new RuntimeException("Username is required!");
        }

        if(user.getEmail() == null || user.getEmail().isBlank()) {

            throw new RuntimeException("Email is required!");
        }

        if(user.getPassword() == null || user.getPassword().isBlank()) {

            throw new RuntimeException("Password is required!");
        }

        if(user.getPassword().length() < 8) {

            throw new RuntimeException("Password must contain at least 8 characters!");
        }

        if(!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            throw new RuntimeException("Invalid email format!");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }
    }

    private void validateForUpdate(User user) {

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new RuntimeException("Username is required!");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new RuntimeException("Email is required!");
        }

        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new RuntimeException("Invalid email format!");
        }

        User existingUsername = userRepository.findByUsername(user.getUsername()).orElse(null);

        if (existingUsername != null && !existingUsername.getId().equals(user.getId())) {

            throw new RuntimeException("Username already exists!");
        }

        User existingEmail = userRepository.findByEmail(user.getEmail()).orElse(null);

        if (existingEmail != null && !existingEmail.getId().equals(user.getId())) {

            throw new RuntimeException("Email already exists!");
        }
    }

    public User updateProfile(User user) {

        validateForUpdate(user);

        User existingUser =
                findById(user.getId());

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setBirthDate(user.getBirthDate());

        if(user.getNewPassword() != null
                && !user.getNewPassword().isBlank()) {

            if(!user.getNewPassword()
                    .equals(user.getConfirmPassword())) {

                throw new RuntimeException(
                        "Passwords do not match!");
            }

            if(user.getNewPassword().length() < 8) {

                throw new RuntimeException(
                        "Password must contain at least 8 characters!");
            }

            existingUser.setPassword(user.getNewPassword());
        }

        return userRepository.save(existingUser);
    }

}