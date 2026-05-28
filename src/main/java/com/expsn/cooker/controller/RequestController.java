package com.expsn.cooker.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expsn.cooker.model.RecipeRequest;
import com.expsn.cooker.model.RecipeRequestResponse;
import com.expsn.cooker.service.RequestService;
import com.expsn.cooker.util.ControllerAuthUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    // requires logged user
    @PostMapping
    public ResponseEntity<RecipeRequest> create(@RequestBody RecipeRequest req, Authentication authentication) {
        req.setRequesterId(ControllerAuthUtils.resolveRequiredUserId(authentication));
        req.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.createRequest(req));
    }

    // requires logged user
    @GetMapping("/{id}")
    public ResponseEntity<RecipeRequest> getById(@PathVariable String id, Authentication authentication) {
        return ResponseEntity.ok(requestService.getRequestById(id, ControllerAuthUtils.resolveCurrentUserId(authentication)));
    }

    // requires logged user
    @GetMapping("/active")
    public ResponseEntity<List<RecipeRequest>> getActive(Authentication authentication) {
        // Service filtra createdAt > 30 dias atrás
        return ResponseEntity.ok(requestService.getActiveRequests(ControllerAuthUtils.resolveRequiredUserId(authentication)));
    }

    // requires logged user
    @PostMapping("/{id}/respond")
    public ResponseEntity<Void> respond(
            @PathVariable String id,
            @RequestBody RecipeRequestResponse response,
            Authentication authentication) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        response.setResponderId(userId);
        requestService.save(id, response, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
