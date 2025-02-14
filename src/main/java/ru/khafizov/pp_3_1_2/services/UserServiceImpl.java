package ru.khafizov.pp_3_1_2.services;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import ru.khafizov.pp_3_1_2.configs.EncoderConfig;
import ru.khafizov.pp_3_1_2.models.User;
import ru.khafizov.pp_3_1_2.repositories.UserRepositoriy;

import java.util.List;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepositoriy userRepositoriy;
    private final EncoderConfig encoderConfig;

    @Autowired
    public UserServiceImpl(UserRepositoriy userRepositoriy, EncoderConfig encoderConfig) {
        this.userRepositoriy = userRepositoriy;
        this.encoderConfig = encoderConfig;
    }


    @Override
    public User findByUsername(String username) { //метод вне SpringSecurity, просто возвращает юзера по имени
        return userRepositoriy.findByUsername(username);
    }

    @Override
    public User findByEmail(String email){
        return userRepositoriy.findByEmail(email);
    }


    @Override
    public void save(User user) {
        if (findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        user.setPassword(encoderConfig.passwordEncoder().encode((user.getPassword())));
        userRepositoriy.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepositoriy.findAll();
    }

    @Override
    public User findById(Integer id) {
        return userRepositoriy.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователь с ID " + id + " не найден"));
    }

    @Override
    public void updateUser(User updateUser) {
        User existUser = userRepositoriy.findById(updateUser.getId()).orElseThrow(() ->
                new EntityNotFoundException("Пользователь с ID = " + updateUser.getId() + "не найден"));

        existUser.setUsername(updateUser.getUsername());
        existUser.setLastname(updateUser.getLastname());
        existUser.setAge(updateUser.getAge());
        existUser.setRoles(updateUser.getRoles());
        existUser.setEmail(updateUser.getEmail());

        if (!updateUser.getPassword().isEmpty() && !updateUser.getPassword().equals(existUser.getPassword())) {
            existUser.setPassword(updateUser.getPassword());
        }

        userRepositoriy.save(existUser);
    }

    @Override
    public void validateUser(User user) {
        User exitingUser = findByEmail(user.getEmail());
        if (exitingUser != null && !exitingUser.getId().equals(user.getId())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
    }

    @Override
    public void deleteById(Integer id) {
        userRepositoriy.deleteById(id);
    }


}
