package com.workforge.authservice.application.service;

import com.workforge.authservice.api.dto.request.LoginRequest;
import com.workforge.authservice.api.dto.request.RegisterRequest;
import com.workforge.authservice.api.dto.response.AuthResponse;
import com.workforge.authservice.application.exception.EmailAlreadyExistsException;
import com.workforge.authservice.domain.model.Role;
import com.workforge.authservice.domain.model.User;
import com.workforge.authservice.infrastructure.persistence.UserRepository;
import com.workforge.authservice.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldThrowEmailAlreadyExistsException_whenEmailAlreadyExists() {
        //GIVEN
        RegisterRequest request = RegisterRequest.builder()
                .email("test@test.com")
                .password("password123")
                .role(Role.RECRUITER)
                .build();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(User.builder().build()));

        // WHEN / THEN
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(request.getEmail());
    }

    @Test
    void register_shouldReturnToken_whenRegistrationSuccessful() {
        //GIVEN
        RegisterRequest request = RegisterRequest.builder()
                .email("test@test.com")
                .password("testpassword")
                .role(Role.RECRUITER)
                .build();

        User savedUser = User.builder()
                .email("test@test.com")
                .role(Role.RECRUITER)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(savedUser)).thenReturn("mockToken");

        //WHEN
        AuthResponse result = authService.register(request);

        //THEN
        assertThat(result.getToken()).isEqualTo("mockToken");
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        // GIVEN
        LoginRequest request = LoginRequest.builder()
                .email("test@test.com")
                .password("password123")
                .build();

        User user = User.builder()
                .email("test@test.com")
                .role(Role.RECRUITER)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mockToken");

        // WHEN
        AuthResponse result = authService.login(request);

        // THEN
        assertThat(result.getToken()).isEqualTo("mockToken");
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }
}
