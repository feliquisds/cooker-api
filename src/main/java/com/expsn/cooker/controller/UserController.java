package com.expsn.cooker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expsn.cooker.model.User;
import com.expsn.cooker.model.dto.UserPublic;
import com.expsn.cooker.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // does not require logged user, but if provided, show the profile if userId is the same as the requested profile
    @GetMapping("/profile/{handle}")
    public ResponseEntity<UserPublic> getPublicProfile(
            @PathVariable String handle,
            Authentication authentication) {
        String currentUserId = resolveCurrentUserId(authentication);
        return ResponseEntity.ok(userService.getUserProfile(handle, currentUserId));
    }

    @GetMapping("/profile/id/{id}")
    public ResponseEntity<UserPublic> getPublicProfileById(
            @PathVariable String id,
            Authentication authentication) {
        String currentUserId = resolveCurrentUserId(authentication); 
        return ResponseEntity.ok(userService.getUserProfileById(id, currentUserId));
    }

    // requires logged user
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {
        String userId = resolveRequiredUserId(authentication);
        return ResponseEntity.ok(userService.findById(userId));
    }

    // requires logged user
    @PatchMapping("/privacy")
    public ResponseEntity<Void> togglePrivacy(Authentication authentication, @RequestParam boolean isPrivate) {
        String userId = resolveRequiredUserId(authentication);
        userService.updatePrivacy(userId, isPrivate);
        return ResponseEntity.noContent().build();
    }

    private String resolveCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getName();
    }

    private String resolveRequiredUserId(Authentication authentication) {
        String userId = resolveCurrentUserId(authentication);
        if (userId == null) {
            throw new RuntimeException("Usuário não autenticado");
        }

        return userId;
    }
}
