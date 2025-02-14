package ru.khafizov.pp_3_1_2.services;

import ru.khafizov.pp_3_1_2.models.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);

    void save(User user);

    List<User> findAll();

    User findById(Integer id);

    void validateUser(User user);

    void deleteById(Integer id);

    void updateUser(User updateUser);

    User findByEmail(String email);
}
