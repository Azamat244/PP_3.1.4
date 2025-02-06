package ru.khafizov.pp_3_1_2.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import ru.khafizov.pp_3_1_2.services.UserService;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final SuccessUserHandler successUserHandler;
    private final UserService userService;
    private final EncoderConfig encoderConfig;

    @Autowired
    public WebSecurityConfig(SuccessUserHandler successUserHandler, UserService userService, EncoderConfig encoderConfig) {
        this.successUserHandler = successUserHandler;
        this.userService = userService;
        this.encoderConfig = encoderConfig;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.
                authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/user/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers("/").permitAll()

                )
                .formLogin(form -> form //логика перенаправления после успешной аутентификации
                        .successHandler(successUserHandler)
                        .permitAll()
                )
                .logout(logout -> logout //логика логаута
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .build();
    }


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(encoderConfig.passwordEncoder());
        authenticationProvider.setUserDetailsService(userService);
        return authenticationProvider;
    }
}