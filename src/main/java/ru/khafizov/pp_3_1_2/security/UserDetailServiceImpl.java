package ru.khafizov.pp_3_1_2.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.khafizov.pp_3_1_2.models.User;
import ru.khafizov.pp_3_1_2.repositories.UserRepositoriy;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepositoriy userRepositoriy;

    public UserDetailServiceImpl(UserRepositoriy userRepositoriy) {
        this.userRepositoriy = userRepositoriy;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { //SpringSecurity, использует именно этот метод при авторизации чтобы получить UserDetails
        User user = userRepositoriy.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User " + email + " not found");
        }
        return user;
    }
}
