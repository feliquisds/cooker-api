package com.expsn.cooker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expsn.cooker.model.Text;
import com.expsn.cooker.service.TextService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/texts")
@RequiredArgsConstructor
public class TextController {

    private final TextService textService;

    @GetMapping("/{id}")
    public ResponseEntity<Text> getText(@PathVariable String id, Authentication authentication) {
        String userId = resolveCurrentUserId(authentication);
        return ResponseEntity.ok(textService.getTextById(id, userId));
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
