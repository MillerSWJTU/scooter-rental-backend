package scooterrent.service;

import scooterrent.dto.LoginRequestDTO;
import scooterrent.dto.LoginResponse;
import scooterrent.dto.RegisterRequestDTO;
import scooterrent.entity.Role;
import scooterrent.entity.User;
import scooterrent.repository.RoleRepository;
import scooterrent.repository.UserRepository;
import scooterrent.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public LoginResponse register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("User role not found"));
        user.setRole(userRole);

        userRepository.save(user);

        String token = jwtTokenUtil.generateToken(user.getUsername());
        return new LoginResponse(token, user.getUsername(), user.getRole().getName(), user.getEmail(), user.getPhone());
    }

    public LoginResponse login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtTokenUtil.generateToken(user.getUsername());
        return new LoginResponse(token, user.getUsername(), user.getRole().getName(), user.getEmail(), user.getPhone());
    }
} 