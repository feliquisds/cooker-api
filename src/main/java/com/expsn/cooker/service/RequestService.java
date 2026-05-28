package com.expsn.cooker.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.expsn.cooker.client.NotificationClient;
import com.expsn.cooker.exception.BusinessException;
import com.expsn.cooker.exception.ItemException;
import com.expsn.cooker.model.RecipeRequest;
import com.expsn.cooker.model.RecipeRequestResponse;
import com.expsn.cooker.repository.RequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final NotificationClient notificationClient;

    public RecipeRequest createRequest(RecipeRequest request) {
        if (request.getResponses() == null) {
            request.setResponses(new ArrayList<>());
        }

        RecipeRequest saved = requestRepository.save(request);
        notificationClient.queueNotifyRequestInterest(saved);
        return saved;
    }

    public RecipeRequest getRequestById(String id, String userId) {
        RecipeRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ItemException("Solicitação não encontrada"));

        if (userId != null && request.getRequesterId().equals(userId)) {
            return request;
        }

        throw new BusinessException("Acesso negado a esta solicitação");
    }

    public RecipeRequestResponse save(String requestId, RecipeRequestResponse response, String userId) {
        RecipeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ItemException("Solicitação não encontrada"));

        if (userId == null || request.getRequesterId().equals(userId)) {
            throw new BusinessException("Acesso negado a esta solicitação");
        }

        if (request.getResponses() == null) {
            request.setResponses(new ArrayList<>());
        }

        response.setCreatedAt(LocalDateTime.now());
        request.getResponses().add(response);
        requestRepository.save(request);
        notificationClient.queueNotifyRequestResponse(response);
        return response;
    }

    public List<RecipeRequest> getActiveRequests(String userId) {
        // Busca apenas o que foi criado nos últimos 30 dias e não foi fechado manualmente
        LocalDateTime limit = LocalDateTime.now().minusDays(30);
        List<RecipeRequest> activeRequests = requestRepository.findByCreatedAtAfterAndManuallyClosedFalse(limit);

        // Se o usuário estiver logado
        if (userId != null) {
            activeRequests = activeRequests.stream()
                    .filter(req -> req.getRequesterId().equals(userId))
                    .toList();
        }

        return activeRequests;
    }
}
