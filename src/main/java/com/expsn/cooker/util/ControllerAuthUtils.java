package com.expsn.cooker.util;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.http.HttpStatus;

import com.expsn.cooker.exception.BusinessException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ControllerAuthUtils {

    public static String resolveCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getName();
    }

    public static String resolveRequiredUserId(Authentication authentication) {
        String userId = resolveCurrentUserId(authentication);
        if (userId == null) {
            throw new BusinessException("Usuário não autenticado", HttpStatus.UNAUTHORIZED);
        }

        return userId;
    }
}