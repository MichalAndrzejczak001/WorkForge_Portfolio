package com.workforge.authservice.application.service;

import com.workforge.authservice.api.dto.request.LoginRequest;
import com.workforge.authservice.api.dto.request.RegisterRequest;
import com.workforge.authservice.api.dto.response.AuthResponse;
import com.workforge.authservice.application.exception.EmailAlreadyExistsException;
import com.workforge.authservice.application.exception.UserNotFoundException;
import com.workforge.authservice.domain.model.User;
import com.workforge.authservice.infrastructure.persistence.UserRepository;
import com.workforge.authservice.infrastructure.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email " + request.getEmail() + " already exists");
        }
        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();
        User savedUser = userRepository.save(newUser);
        String token = jwtService.generateToken(savedUser);
        return AuthResponse.builder()
                .token(token)
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User with email " + request.getEmail()
                        + " doesn't exist."));
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
