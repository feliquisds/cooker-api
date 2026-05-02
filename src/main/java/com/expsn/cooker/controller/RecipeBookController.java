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

import com.expsn.cooker.model.RecipeBook;
import com.expsn.cooker.service.RecipeBookService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class RecipeBookController {

    private final RecipeBookService recipeBookService;

    @PostMapping
    public ResponseEntity<RecipeBook> create(@RequestBody RecipeBook book, @RequestHeader("X-User-ID") String userId) {
        book.setOwnerId(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeBookService.save(book));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeBook> getBookDetail(@PathVariable String id) {
        // Aqui a Service deve retornar o livro com os nomes das receitas hidratados
        return ResponseEntity.ok(recipeBookService.getHydratedBook(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecipeBook>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String authorHandle) {
        return ResponseEntity.ok(recipeBookService.searchBooks(title, tags, authorHandle));
    }
}
