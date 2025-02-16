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

    @GetMapping("/userInfo/{id}")
    public String userInfo(@PathVariable("id") Integer id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "showInfoForAdmin";
    }

    @GetMapping("/user/create") //страница создания пользователя.
    public String createPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        List<Role> roles = roleService.findAll();
        model.addAttribute("allRoles", roles);
        return "create";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") @Valid User user,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            List<Role> roles = roleService.findAll();
            model.addAttribute("allRoles", roles);
            return "create";
        } else {
            try {
                userService.save(user);
            } catch (RuntimeException e) {
                bindingResult.rejectValue("username", "error.user", e.getMessage());
                List<Role> roles = roleService.findAll();
                model.addAttribute("allRoles", roles);
                return "/create";
            }
            return "redirect:/admin";
        }
    }


    //страница где будет показана информация о пользователе с возможностью обновления и удаления
    @GetMapping("/edit/{id}")
    public String getEditForm(@PathVariable("id") Integer id, Model model) {
        User user = userService.findById(id);
        List<Role> roles = roleService.findAll();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "editPage";


    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        try {
            userService.validateUser(user);
        } catch (RuntimeException e) {
            bindingResult.rejectValue("username", "error.user", e.getMessage());
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleService.findAll());
            return "editPage";
        }
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }
}
