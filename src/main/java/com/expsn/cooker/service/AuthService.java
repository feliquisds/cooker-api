package com.expsn.cooker.service;

import com.expsn.cooker.exception.CookerException;
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
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CookerException("Email is already registered");
        }

        if (userRepository.findByHandle(request.getHandle()).isPresent()) {
            throw new CookerException("Handle is already taken");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .handle(request.getHandle())
                .isPrivate(false)
                .favoriteRecipeIds(new ArrayList<>())
                .savedBookIds(new ArrayList<>())
                .requestNotificationTags(new ArrayList<>())
                .recipeNotificationTags(new ArrayList<>())
                .rating(0)
                .build();

        User savedUser = userRepository.save(user);

        authenticate(request.getEmail(), request.getPassword());
        return buildAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticate(request.getEmail(), request.getPassword());
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CookerException("Usuário não encontrado"));

            return buildAuthResponse(user);

        } catch (AuthenticationException _) {
            throw new CookerException("Email ou senha inválidos");
        }
    }

    private Authentication authenticate(String email, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .token(jwtTokenProvider.generateTokenFromUserId(user.getId()))
                .email(user.getEmail())
                .name(user.getName())
                .userId(user.getId())
                .build();
    }
}
