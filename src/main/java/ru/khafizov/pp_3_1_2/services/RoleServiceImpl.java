package ru.khafizov.pp_3_1_2.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.khafizov.pp_3_1_2.models.Role;
import ru.khafizov.pp_3_1_2.repositories.RoleRepository;

import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
