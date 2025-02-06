package ru.khafizov.pp_3_1_2.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.khafizov.pp_3_1_2.configs.EncoderConfig;
import ru.khafizov.pp_3_1_2.models.Role;
import ru.khafizov.pp_3_1_2.models.User;
import ru.khafizov.pp_3_1_2.repositories.RoleRepository;
import ru.khafizov.pp_3_1_2.repositories.UserRepositoriy;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private final UserRepositoriy userRepositoriy;
    private final RoleRepository roleRepository;
    private final EncoderConfig encoderConfig;

    @Autowired
    public UserService(UserRepositoriy userRepositoriy, RoleRepository roleRepository, EncoderConfig encoderConfig) {
        this.userRepositoriy = userRepositoriy;
        this.roleRepository = roleRepository;
        this.encoderConfig = encoderConfig;
    }


    public User findByUsername(String username) { //метод вне SpringSecurity, просто возвращает юзера по имени
        return userRepositoriy.findByUsername(username);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { //SpringSecurity, использует именно этот метод при авторизации чтобы получить UserDetails
        User user = findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        return user;
    }

    public void save(User user) {
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(user.getRoles()
                .stream()
                .map(role -> role.getId())
                .toList()));
        user.setRoles(roles);
        user.setPassword(encoderConfig.passwordEncoder().encode((user.getPassword())));
        userRepositoriy.save(user);
    }

    @PostConstruct
    public void initAddRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ROLE_USER"));
            roleRepository.save(new Role("ROLE_ADMIN"));
        }
    }
}
