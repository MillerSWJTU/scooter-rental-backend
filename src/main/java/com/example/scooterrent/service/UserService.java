package com.example.scooterrent.service;

import com.example.scooterrent.dto.LoginRequestDTO;
import com.example.scooterrent.dto.LoginResponse;
import com.example.scooterrent.dto.RegisterRequestDTO;
import com.example.scooterrent.dto.UserDTO;
import com.example.scooterrent.entity.User;
import com.example.scooterrent.entity.Role;
import com.example.scooterrent.repository.UserRepository;
import com.example.scooterrent.repository.RoleRepository;
import com.example.scooterrent.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Transactional
    public UserDTO register(RegisterRequestDTO registrationDTO) {
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("用户角色不存在"));

        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setEmail(registrationDTO.getEmail());
        user.setPhone(registrationDTO.getPhone());
        user.setRole(userRole);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Transactional
    public UserDTO registerAdmin(RegisterRequestDTO registrationDTO) {
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("管理员角色不存在"));

        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setEmail(registrationDTO.getEmail());
        user.setPhone(registrationDTO.getPhone());
        user.setRole(adminRole);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public LoginResponse login(LoginRequestDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(loginDTO.getUsername())
            .orElseThrow(() -> new RuntimeException("用户不存在"));

        String token = jwtTokenUtil.generateToken(user.getUsername());
        return new LoginResponse(token, user.getUsername(), user.getRole().getName(), user.getEmail(), user.getPhone());
    }

    public UserDTO getUserById(Integer id) {
        throw new UnsupportedOperationException("Method not supported. Use getUserByUsername instead.");
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateUser(String username, UserDTO userDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        
        // 更新角色
        if (userDTO.getRole() != null) {
            Role role = roleRepository.findByName(userDTO.getRole())
                    .orElseThrow(() -> new RuntimeException("角色不存在"));
            user.setRole(role);
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Integer id) {
        throw new UnsupportedOperationException("Method not supported. Use deleteUser(String username) instead.");
    }

    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        userRepository.delete(user);
    }

    @Transactional
    public void setUserRole(String username, String roleName) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new RuntimeException("角色不存在"));
        user.setRole(role);
        userRepository.save(user);
    }

    @Transactional
    public UserDTO rechargeBalance(String username, java.math.BigDecimal amount) {
        if (amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Recharge amount must be positive");
        }
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setBalance(user.getBalance().add(amount));
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole().getName());
        dto.setBalance(user.getBalance());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
} 