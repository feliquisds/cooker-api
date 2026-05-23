package com.expsn.cooker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.expsn.cooker.exception.CookerException;
import com.expsn.cooker.model.RecipeRequest;
import com.expsn.cooker.model.RecipeRequestResponse;
import com.expsn.cooker.repository.RequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    // private final NotificationService notificationService; NOSONAR

    public RecipeRequest createRequest(RecipeRequest request) {
        //RecipeRequest saved = requestRepository.save(request); NOSONAR

        // Regra de Negócio: Notificar usuários interessados nas tags
        // notificationService.notifyUsersWithTags(request.getTags()); NOSONAR

        //return saved; NOSONAR

        return requestRepository.save(request);
    }

    public void respondToRequest(String requestId, RecipeRequestResponse response) {
        RecipeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new CookerException("Solicitação não encontrada"));

        request.getResponses().add(response);
        requestRepository.save(request);
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
