package com.digitalheroes.golfplatform.services;

import com.digitalheroes.golfplatform.dto.AuthDtos;
import com.digitalheroes.golfplatform.exceptions.InvalidCredentialsException;
import com.digitalheroes.golfplatform.models.User;
import com.digitalheroes.golfplatform.models.UserRole;
import com.digitalheroes.golfplatform.repositories.UserRepository;
import com.digitalheroes.golfplatform.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.SUBSCRIBER);
        user.setActive(true);
        userRepository.save(user);
        return new AuthDtos.AuthResponse(jwtUtil.generateToken(user.getEmail()));
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        return new AuthDtos.AuthResponse(jwtUtil.generateToken(user.getEmail()));
    }
}
