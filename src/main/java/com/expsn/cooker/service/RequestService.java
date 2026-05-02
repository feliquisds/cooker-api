package com.expsn.cooker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.expsn.cooker.model.RecipeRequest;
import com.expsn.cooker.model.RecipeRequestResponse;
import com.expsn.cooker.repository.RequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    // private final NotificationService notificationService;

    public RecipeRequest createRequest(RecipeRequest request) {
        RecipeRequest saved = requestRepository.save(request);

        // Regra de Negócio: Notificar usuários interessados nas tags
        // notificationService.notifyUsersWithTags(request.getTags());

        return saved;
    }

    public void respondToRequest(String requestId, RecipeRequestResponse response) {
        RecipeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Requisição expirada ou inexistente"));

        request.getResponses().add(response);
        requestRepository.save(request);
    }

    public List<RecipeRequest> getActiveRequests() {
        // Busca apenas o que foi criado nos últimos 30 dias e não foi fechado manualmente
        LocalDateTime limit = LocalDateTime.now().minusDays(30);
        return requestRepository.findByCreatedAtAfterAndManuallyClosedFalse(limit);
    }
}
