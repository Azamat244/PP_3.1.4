package ru.khafizov.pp_3_1_2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.khafizov.pp_3_1_2.models.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
