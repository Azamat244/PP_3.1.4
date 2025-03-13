package ru.khafizov.pp_3_1_2.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.khafizov.pp_3_1_2.models.User;
import ru.khafizov.pp_3_1_2.services.UserService;

import java.util.List;


@RestController
public class HelloController {

    private final UserService userService;

    public HelloController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/user123") //гостевая страница
    public ResponseEntity<List<User>> pageForAll() {
        List<User> userList = userService.findAll();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }


}
