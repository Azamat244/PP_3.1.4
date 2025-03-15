package ru.khafizov.pp_3_1_2.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class AuthController {

    @GetMapping("/login")
    public String getLoginForm() {
        return "loginForm";
    }
}
