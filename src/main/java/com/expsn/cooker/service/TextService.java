package com.expsn.cooker.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.expsn.cooker.exception.BusinessException;
import com.expsn.cooker.exception.ItemException;
import com.expsn.cooker.model.Text;
import com.expsn.cooker.model.User;
import com.expsn.cooker.repository.TextRepository;
import com.expsn.cooker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TextService {

    private final TextRepository textRepository;
    private final UserRepository userRepository;

    public Text getTextById(String id, String userId) {
        Text text = textRepository.findById(id)
            .orElseThrow(() -> new ItemException("Texto não encontrado"));
        User owner = userRepository.findById(text.getAuthorId())
            .orElseThrow(() -> new ItemException("Autor do texto não encontrado"));

        if (!text.isPublic() && !text.getAuthorId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para visualizar este texto", org.springframework.http.HttpStatus.PRECONDITION_FAILED);
        }
        if (owner.isPrivate() && !owner.getId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para visualizar este texto", org.springframework.http.HttpStatus.PRECONDITION_FAILED);
        }

        return text;
    }

    public Text save(Text text, String userId) {
        if (text.getId() != null) {
            Text existing = textRepository.findById(text.getId())
                    .orElseThrow(() -> new ItemException("Texto não encontrado"));
            if (!existing.getAuthorId().equals(userId)) {
                throw new BusinessException("Você não tem permissão para editar este texto", org.springframework.http.HttpStatus.PRECONDITION_FAILED);
            }
            text.setAuthorId(existing.getAuthorId());
            text.setCreatedAt(existing.getCreatedAt());
        } else {
            text.setAuthorId(userId);
            text.setCreatedAt(LocalDateTime.now());
        }

        text.setUpdatedAt(LocalDateTime.now());
        return textRepository.save(text);
    }

    public void delete(String id, String userId) {
        Text text = textRepository.findById(id)
                .orElseThrow(() -> new ItemException("Texto não encontrado"));

        if (!text.getAuthorId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para apagar este texto", org.springframework.http.HttpStatus.PRECONDITION_FAILED);
        }

        textRepository.deleteById(id);
    }
    
}
