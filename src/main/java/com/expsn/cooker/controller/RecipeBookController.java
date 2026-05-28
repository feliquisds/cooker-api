package com.expsn.cooker.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expsn.cooker.model.RecipeBook;
import com.expsn.cooker.service.RecipeBookService;
import com.expsn.cooker.util.ControllerAuthUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class RecipeBookController {

    private final RecipeBookService recipeBookService;

    // requires logged user
    @PostMapping
    public ResponseEntity<RecipeBook> create(@RequestBody RecipeBook book, Authentication authentication) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        return ResponseEntity.ok(recipeBookService.save(book, userId));
    }

    // does not require logged user, but if provided, show the book if it's public or if it's private and belongs to the user
    @GetMapping("/{id}")
    public ResponseEntity<RecipeBook> getBookById(@PathVariable String id, Authentication authentication) {
        // Aqui a Service deve retornar o livro com os nomes das receitas hidratados
        String userId = ControllerAuthUtils.resolveCurrentUserId(authentication);
        return ResponseEntity.ok(recipeBookService.getBookById(id, userId));
    }

    // does not require logged user, but if provided, list only public books + private books of the user
    @GetMapping("/search")
    public ResponseEntity<List<RecipeBook>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String authorHandle,
            Authentication authentication) {
        return ResponseEntity.ok(recipeBookService.searchBooks(title, tags, authorHandle, ControllerAuthUtils.resolveCurrentUserId(authentication)));
    }

    @GetMapping("/saved")
    public ResponseEntity<List<RecipeBook>> getSavedBooks(Authentication authentication) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        return ResponseEntity.ok(recipeBookService.getSavedBooks(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        recipeBookService.delete(id, userId);
        return ResponseEntity.ok().build();
    }
}
