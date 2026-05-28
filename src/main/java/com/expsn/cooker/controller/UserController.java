package com.expsn.cooker.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expsn.cooker.model.User;
import com.expsn.cooker.model.Review;
import com.expsn.cooker.model.dto.UserPublic;
import com.expsn.cooker.service.ReviewService;
import com.expsn.cooker.service.UserService;
import com.expsn.cooker.util.ControllerAuthUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;

    // does not require logged user, but if provided, show the profile if userId is the same as the requested profile
    @GetMapping("/profile/{handle}")
    public ResponseEntity<UserPublic> getPublicProfile(
            @PathVariable String handle,
            Authentication authentication) {
        String currentUserId = ControllerAuthUtils.resolveCurrentUserId(authentication);
        return ResponseEntity.ok(userService.getUserProfile(handle, currentUserId));
    }

    @GetMapping("/profile/id/{id}")
    public ResponseEntity<UserPublic> getPublicProfileById(
            @PathVariable String id,
            Authentication authentication) {
        String currentUserId = ControllerAuthUtils.resolveCurrentUserId(authentication); 
        return ResponseEntity.ok(userService.getUserProfileById(id, currentUserId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserPublic> getPublicProfileByIdAlias(
            @PathVariable String id,
            Authentication authentication) {
        String currentUserId = ControllerAuthUtils.resolveCurrentUserId(authentication);
        return ResponseEntity.ok(userService.getUserProfileById(id, currentUserId));
    }

    // requires logged user
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        return ResponseEntity.ok(userService.findById(userId));
    }

    @GetMapping("/me/reviews")
    public ResponseEntity<List<Review>> getMyReviews(Authentication authentication) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        return ResponseEntity.ok(reviewService.getReviewsByUserId(userId));
    }

    @PostMapping("/me")
    public ResponseEntity<User> saveMyProfile(@RequestBody User user, Authentication authentication) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        return ResponseEntity.ok(userService.save(user, userId));
    }

    // requires logged user
    @PatchMapping("/privacy")
    public ResponseEntity<Void> togglePrivacy(Authentication authentication, @RequestParam boolean isPrivate) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        userService.updatePrivacy(userId, isPrivate);
        return ResponseEntity.noContent().build();
    }
}
