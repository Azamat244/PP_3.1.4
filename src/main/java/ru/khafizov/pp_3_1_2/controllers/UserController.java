package ru.khafizov.pp_3_1_2.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.khafizov.pp_3_1_2.models.User;
import ru.khafizov.pp_3_1_2.services.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/profile_user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<User> userPage(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }
}
