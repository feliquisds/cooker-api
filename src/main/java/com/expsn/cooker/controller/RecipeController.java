package com.expsn.cooker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) String authorHandle) {
        return ResponseEntity.ok(recipeService.searchRecipes(title, tags, difficulty, authorHandle));
    }

    @PostMapping
    public ResponseEntity<Recipe> create(@RequestBody Recipe recipe, @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.createRecipe(recipe, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getById(@PathVariable String id) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }
    
}
