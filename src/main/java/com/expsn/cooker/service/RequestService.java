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
import com.expsn.cooker.model.User;
import com.expsn.cooker.repository.RequestRepository;
import com.expsn.cooker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final NotificationClient notificationClient;

    public RecipeRequest createRequest(RecipeRequest request, String userId) {
        request.setCreatedAt(LocalDateTime.now());
        request.setRequesterId(userId);
        RecipeRequest saved = requestRepository.save(request);
        notificationClient.queueNotifyRequestInterest(saved);
        return saved;
    }

    public RecipeRequest getRequestById(String id, String userId) {
        RecipeRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ItemException("Solicitação não encontrada"));
        User user = userRepository.findById(request.getRequesterId())
                .orElseThrow(() -> new ItemException("Usuário solicitante não encontrado"));

        if (user.isPrivate() && (userId == null || !request.getRequesterId().equals(userId))) {
            throw new BusinessException("Acesso negado a esta solicitação");
        }

        return request;
    }

    public RecipeRequestResponse respond(String requestId, RecipeRequestResponse response, String userId) {
        RecipeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ItemException("Solicitação não encontrada"));
        User user = userRepository.findById(request.getRequesterId())
                .orElseThrow(() -> new ItemException("Usuário não encontrado"));

        if (user.isPrivate() && !request.getRequesterId().equals(userId)) {
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
