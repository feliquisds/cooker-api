package com.expsn.cooker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expsn.cooker.model.Text;
import com.expsn.cooker.service.TextService;
import com.expsn.cooker.util.ControllerAuthUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/texts")
@RequiredArgsConstructor
public class TextController {

    private final TextService textService;

    @GetMapping("/{id}")
    public ResponseEntity<Text> getText(@PathVariable String id, Authentication authentication) {
        String userId = ControllerAuthUtils.resolveCurrentUserId(authentication);
        return ResponseEntity.ok(textService.getTextById(id, userId));
    }

    @PostMapping
    public ResponseEntity<Text> save(@RequestBody Text text, Authentication authentication) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        return ResponseEntity.ok(textService.save(text, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) {
        String userId = ControllerAuthUtils.resolveRequiredUserId(authentication);
        textService.delete(id, userId);
        return ResponseEntity.ok().build();
    }
}
