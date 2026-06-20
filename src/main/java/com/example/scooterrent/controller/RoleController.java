package com.example.scooterrent.controller;

import com.example.scooterrent.entity.Role;
import com.example.scooterrent.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Role>> getAllRoles() {
        logger.info("获取所有角色");
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        logger.info("通过ID获取角色: {}", id);
        Optional<Role> role = roleService.getRoleById(id);
        return role.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        logger.info("创建新角色: {}", role.getName());

        if (roleService.existsByName(role.getName())) {
            logger.warn("角色名 {} 已存在", role.getName());
            return ResponseEntity.badRequest().build();
        }

        Role savedRole = roleService.save(role);
        logger.info("角色创建成功: {}, ID: {}", savedRole.getName(), savedRole.getId());
        return ResponseEntity.ok(savedRole);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        logger.info("更新ID为 {} 的角色", id);

        if (!roleService.existsById(id)) {
            logger.warn("未找到ID为 {} 的角色", id);
            return ResponseEntity.notFound().build();
        }

        role.setId(id);
        Role updatedRole = roleService.save(role);
        logger.info("角色更新成功: {}", updatedRole.getName());
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        logger.info("删除ID为 {} 的角色", id);

        if (!roleService.existsById(id)) {
            logger.warn("未找到ID为 {} 的角色", id);
            return ResponseEntity.notFound().build();
        }

        roleService.deleteById(id);
        logger.info("角色删除成功");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/init")
    public ResponseEntity<String> initDefaultRoles() {
        logger.info("初始化默认角色");
        roleService.initDefaultRoles();
        return ResponseEntity.ok("默认角色初始化成功");
    }
}
