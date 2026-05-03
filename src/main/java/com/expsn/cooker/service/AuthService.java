package com.expsn.cooker.service;

import com.expsn.cooker.model.User;
import com.expsn.cooker.model.dto.AuthResponse;
import com.expsn.cooker.model.dto.LoginRequest;
import com.expsn.cooker.model.dto.RegisterRequest;
import com.expsn.cooker.repository.UserRepository;
import com.expsn.cooker.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered");
        }

        // Check if handle already exists
        if (userRepository.findByHandle(request.getHandle()).isPresent()) {
            throw new RuntimeException("Handle is already taken");
        }

        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .handle(request.getHandle())
                .isPrivate(false)
                .favoriteRecipeIds(new ArrayList<>())
                .savedBookIds(new ArrayList<>())
                .notificationTags(new ArrayList<>())
                .build();

        User savedUser = userRepository.save(user);

        // Generate token
        String token = jwtTokenProvider.generateTokenFromUserId(savedUser.getId());

        return AuthResponse.builder()
                .token(token)
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .userId(savedUser.getId())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate using the provided email and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Get the authenticated user
                String email = authentication.getName();
                User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate token
                String token = jwtTokenProvider.generateTokenFromUserId(user.getId());

            return AuthResponse.builder()
                    .token(token)
                    .email(user.getEmail())
                    .name(user.getName())
                    .userId(user.getId())
                    .build();

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
}
