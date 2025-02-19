package ru.khafizov.pp_3_1_2.controllers;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.khafizov.pp_3_1_2.models.Role;
import ru.khafizov.pp_3_1_2.models.User;
import ru.khafizov.pp_3_1_2.services.RoleService;
import ru.khafizov.pp_3_1_2.services.UserService;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping  //админ-панель где выводятся все юзеры
    public String admin(Model model, Principal principal) {
        List<User> users = userService.findAll();
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("users", users);
        model.addAttribute("userName", user);
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", roleService.findAll());
        return "adminPage";
    }


    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user, Model model) {
        userService.save(user);
        return "redirect:/admin";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute("user") User user, Model model) {
        userService.validateUser(user);
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }
}
