package com.expsn.cooker.service;

import org.springframework.stereotype.Service;

import com.expsn.cooker.exception.CookerException;
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
            .orElseThrow(() -> new CookerException("Texto não encontrado"));
        User owner = userRepository.findById(text.getAuthorId())
            .orElseThrow(() -> new CookerException("Autor do texto não encontrado"));

        if (!text.isPublic() && !text.getAuthorId().equals(userId)) {
            throw new CookerException("Você não tem permissão para visualizar este texto");
        }
        if (owner.isPrivate() && !owner.getId().equals(userId)) {
            throw new CookerException("Você não tem permissão para visualizar este texto");
        }

        return text;
    }
    
}
