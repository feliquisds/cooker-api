package com.expsn.cooker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expsn.cooker.model.Difficulty;
import com.expsn.cooker.model.Recipe;
import com.expsn.cooker.service.RecipeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    // does not require logged user, but if provided, list only public recipes + private recipes of the user
    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) String authorHandle,
            Authentication authentication) {
        return ResponseEntity.ok(recipeService.searchRecipes(title, tags, difficulty, authorHandle, resolveCurrentUserId(authentication)));
    }

    // requires logged user
    @PostMapping
    public ResponseEntity<Recipe> create(@RequestBody Recipe recipe, Authentication authentication) {
        String userId = resolveRequiredUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.createRecipe(recipe, userId));
    }

    // does not require logged user, but if provided, show the recipe if it's public or if it's private and belongs to the user
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getById(@PathVariable String id, Authentication authentication) {
        String userId = resolveCurrentUserId(authentication);
        return ResponseEntity.ok(recipeService.getRecipeById(id, userId));
    }

    @GetMapping("/favorited")
    public ResponseEntity<List<Recipe>> getFavorited(Authentication authentication) {
        String userId = resolveRequiredUserId(authentication);
        return ResponseEntity.ok(recipeService.getMyFavoritedRecipes(userId));
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
