package com.expsn.cooker.service;

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
            throw new BusinessException("Você não tem permissão para visualizar este texto");
        }
        if (owner.isPrivate() && !owner.getId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para visualizar este texto");
        }

        return text;
    }
    
}
