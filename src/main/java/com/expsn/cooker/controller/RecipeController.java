package com.expsn.cooker.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expsn.cooker.model.Difficulty;
import com.expsn.cooker.model.Recipe;
import com.expsn.cooker.model.Review;
import com.expsn.cooker.service.RecipeService;
import com.expsn.cooker.service.ReviewService;
import com.expsn.cooker.util.ControllerAuthUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final ReviewService reviewService;

    // does not require logged user, but if provided, list only public recipes + private recipes of the user
    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) String authorHandle,
            Authentication authentication) {
        return ResponseEntity.ok(recipeService.searchRecipes(title, tags, difficulty, authorHandle, ControllerAuthUtils.resolveCurrentUserId(authentication)));
    }

    // requires logged user
    @PostMapping
    public ResponseEntity<Recipe> create(@RequestBody Recipe recipe, Authentication authentication) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        return ResponseEntity.ok(recipeService.save(recipe, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> update(@PathVariable String id, @RequestBody Recipe recipe, Authentication authentication) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        return ResponseEntity.ok(recipeService.update(id, recipe, userId));
    }

    // does not require logged user, but if provided, show the recipe if it's public or if it's private and belongs to the user
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable String id, Authentication authentication) {
        String userId = ControllerAuthUtils.resolveCurrentUserId(authentication);
        return ResponseEntity.ok(recipeService.getRecipeById(id, userId));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<Review>> getReviewsByRecipeId(@PathVariable String id, Authentication authentication) {
        String userId = ControllerAuthUtils.resolveCurrentUserId(authentication);
        return ResponseEntity.ok(reviewService.getVisibleReviews(id, userId));
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<Review> createReview(
            @PathVariable String id,
            @RequestBody Review review,
            Authentication authentication) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        return ResponseEntity.ok(reviewService.save(id, review, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        recipeService.delete(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/favorited")
    public ResponseEntity<List<Recipe>> getFavorited(Authentication authentication) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        return ResponseEntity.ok(recipeService.getMyFavoritedRecipes(userId));
    }
}
