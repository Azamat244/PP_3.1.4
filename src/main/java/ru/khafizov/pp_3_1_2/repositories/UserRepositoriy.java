package ru.khafizov.pp_3_1_2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.khafizov.pp_3_1_2.models.User;


@Repository
public interface UserRepositoriy extends JpaRepository<User, Integer> {

    User findByUsername(String username);
}
