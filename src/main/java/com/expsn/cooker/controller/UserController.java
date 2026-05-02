package com.expsn.cooker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expsn.cooker.model.User;
import com.expsn.cooker.model.UserPublicDTO;
import com.expsn.cooker.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Visualizar perfil de outra pessoa (respeitando privacidade)
    @GetMapping("/profile/{handle}")
    public ResponseEntity<UserPublicDTO> getPublicProfile(
            @PathVariable String handle,
            @RequestHeader(value = "X-User-ID", required = false) String currentUserId) {
        return ResponseEntity.ok(userService.getUserProfile(handle, currentUserId));
    }

    // Visualizar meus próprios dados completos
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(@RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @PatchMapping("/privacy")
    public ResponseEntity<Void> togglePrivacy(
            @RequestHeader("X-User-ID") String userId,
            @RequestParam boolean isPrivate) {
        userService.updatePrivacy(userId, isPrivate);
        return ResponseEntity.noContent().build();
    }
}
