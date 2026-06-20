package com.example.scooterrent.service;

import com.example.scooterrent.entity.Role;
import com.example.scooterrent.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    public boolean existsById(Long id) {
        return roleRepository.existsById(id);
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public void deleteById(Long id) {
        roleRepository.deleteById(id);
    }

    public void initDefaultRoles() {
        if (!roleRepository.existsByName("ROLE_USER")) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setDescription("Default user role");
            roleRepository.save(userRole);
        }

        if (!roleRepository.existsByName("ROLE_ADMIN")) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setDescription("Administrator role");
            roleRepository.save(adminRole);
        }
    }
}
