package ru.khafizov.pp_3_1_2.controllers;


import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.khafizov.pp_3_1_2.models.Role;
import ru.khafizov.pp_3_1_2.models.User;
import ru.khafizov.pp_3_1_2.repositories.RoleRepository;
import ru.khafizov.pp_3_1_2.repositories.UserRepositoriy;
import ru.khafizov.pp_3_1_2.services.UserService;

import java.security.Principal;
import java.util.List;


@Controller
public class HelloController {

    private final UserRepositoriy userRepositoriy;
    private final RoleRepository roleRepository;
    private final UserService userService;

    public HelloController(UserRepositoriy userRepositoriy, RoleRepository roleRepository, UserService userService) {
        this.userRepositoriy = userRepositoriy;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @GetMapping("/") //гостевая страница
    public String pageForAll() {
        return "all";
    }

    @GetMapping("/admin")  //админ-панель где выводятся все юзеры
    public String admin(Model model) {
        List<User> users = userRepositoriy.findAll();
        model.addAttribute("users", users);
        return "adminPage";
    }

    @GetMapping("/admin/userInfo/{id}")
    public String userInfo(@PathVariable("id") Integer id, Model model) {
        User user = userRepositoriy.findById(id).orElse(null);

        if (user != null) {
            model.addAttribute("user", user);
            return "showInfoForAdmin";
        } else return "redirect:/error";

    }


    @GetMapping("/user")
    public String userPage(Model model, Principal principal) {
        User user = userRepositoriy.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "showInfoForUser";
    }


    @GetMapping("/admin/user/create") //страница создания пользователя.
    public String createPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("allRoles", roles);
        return "create";
    }

    @PostMapping("/admin/save")
    public String saveUser(@ModelAttribute("user") @Valid User user,
                           BindingResult bindingResult,
                           Model model) {
        if (userRepositoriy.findByUsername(user.getUsername()) != null) {
            bindingResult.rejectValue("username", "error.user", "Пользователь с таким именем уже существует");
        }
        if (bindingResult.hasErrors()) {
            List<Role> roles = roleRepository.findAll();
            model.addAttribute("allRoles", roles);
            return "/create";
        } else {
            userService.save(user);
            return "redirect:/admin";
        }
    }


    //страница где будет показана информация о пользователе с возможностью обновления и удаления
    @GetMapping("/admin/edit/{id}")
    public String getEditForm(@PathVariable("id") Integer id, Model model) {
        User user = userRepositoriy.findById(id).orElse(null);

        if (user != null) {
            List<Role> roles = roleRepository.findAll();
            model.addAttribute("user", user);
            model.addAttribute("roles", roles);
            return "editPage";
        } else return "redirect:/error";

    }

    @PostMapping("/admin/edit")
    public String editUser(@ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        User exitingUser = userRepositoriy.findByUsername(user.getUsername());
        if (exitingUser != null && !exitingUser.getId().equals(user.getId())) {
            bindingResult.rejectValue("username", "error.user", "Пользователь с таким именем уже существует");
        }
        if (bindingResult.hasErrors()) {
            List<Role> roles = roleRepository.findAll();
            model.addAttribute("roles", roles);
            return "editPage";
        }
        userService.save(user);
        return "redirect:/admin";
    }

    @PostMapping("/admin/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        System.out.println("Удаляем юзера с id " + id);
        userRepositoriy.deleteById(id);
        return "redirect:/admin";
    }
}
