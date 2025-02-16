package ru.khafizov.pp_3_1_2.configs;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import ru.khafizov.pp_3_1_2.models.Role;
import ru.khafizov.pp_3_1_2.models.User;
import ru.khafizov.pp_3_1_2.repositories.RoleRepository;
import ru.khafizov.pp_3_1_2.repositories.UserRepositoriy;

import java.util.Set;

@Component
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepositoriy userRepositoriy;
    private final EncoderConfig encoderConfig;

    public DataInitializer(RoleRepository roleRepository, UserRepositoriy userRepositoriy, EncoderConfig encoderConfig) {
        this.roleRepository = roleRepository;
        this.userRepositoriy = userRepositoriy;
        this.encoderConfig = encoderConfig;
    }


    @PostConstruct
    public void initAddRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ROLE_USER"));
            roleRepository.save(new Role("ROLE_ADMIN"));
        }

        if (userRepositoriy.findByEmail("admin@mail.ru") == null) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            User admin = new User(20, "admin@mail.ru", "admin", encoderConfig.passwordEncoder().encode("100"), "admin");
            admin.setRoles(Set.of(adminRole));
            userRepositoriy.save(admin);
        }
        if (userRepositoriy.findByUsername("user@mail.ru") == null) {
            Role userRole = roleRepository.findByName("ROLE_USER");
            User user = new User(20, "user@mail.ru", "user", encoderConfig.passwordEncoder().encode("100"), "user");
            user.setRoles(Set.of(userRole));
            userRepositoriy.save(user);
        }
    }

}
