package com.expsn.cooker.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expsn.cooker.model.RecipeRequest;
import com.expsn.cooker.model.RecipeRequestResponse;
import com.expsn.cooker.service.RequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RecipeRequest> create(@RequestBody RecipeRequest req, @RequestHeader("X-User-ID") String userId) {
        req.setRequesterId(userId);
        req.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.createRequest(req));
    }

    @GetMapping("/active")
    public ResponseEntity<List<RecipeRequest>> getActive() {
        // Service filtra createdAt > 30 dias atrás
        return ResponseEntity.ok(requestService.getActiveRequests());
    }

    @PostMapping("/{id}/responses")
    public ResponseEntity<Void> respond(
            @PathVariable String id,
            @RequestBody RecipeRequestResponse response,
            @RequestHeader("X-User-ID") String userId) {
        response.setResponderId(userId);
        requestService.respondToRequest(id, response);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
