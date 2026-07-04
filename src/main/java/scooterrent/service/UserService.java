package scooterrent.service;

import scooterrent.dto.RegisterRequestDTO;
import scooterrent.dto.UserDTO;
import scooterrent.entity.User;
import scooterrent.entity.Role;
import scooterrent.exception.BusinessException;
import scooterrent.exception.UserRegistrationException;
import scooterrent.repository.UserRepository;
import scooterrent.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public UserDTO register(RegisterRequestDTO registrationDTO) {
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new UserRegistrationException("用户名已存在");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new UserRegistrationException("用户角色不存在"));

        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setEmail(registrationDTO.getEmail());
        user.setPhone(registrationDTO.getPhone());
        user.setRole(userRole);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
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
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        
        // 更新角色
        if (userDTO.getRole() != null) {
            Role role = roleRepository.findByName(userDTO.getRole())
                    .orElseThrow(() -> new BusinessException(404, "角色不存在"));
            user.setRole(role);
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        userRepository.delete(user);
    }

    @Transactional
    public void updatePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(400, "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void setUserRole(String username, String roleName) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new BusinessException(404, "角色不存在"));
        user.setRole(role);
        userRepository.save(user);
    }

    @Transactional
    public UserDTO rechargeBalance(String username, java.math.BigDecimal amount) {
        if (amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "Recharge amount must be positive");
        }
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        
        user.setBalance(user.getBalance().add(amount));
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
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