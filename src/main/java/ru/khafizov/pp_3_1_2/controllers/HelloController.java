package ru.khafizov.pp_3_1_2.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HelloController {


    @GetMapping("/") //гостевая страница
    public String pageForAll() {
        return "all";
    }


}
