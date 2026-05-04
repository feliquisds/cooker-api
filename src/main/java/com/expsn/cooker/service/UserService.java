package com.expsn.cooker.service;

import org.springframework.stereotype.Service;

import com.expsn.cooker.model.User;
import com.expsn.cooker.model.dto.UserPublic;
import com.expsn.cooker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Usuário não encontrado")
        );
    }

    public void updatePrivacy(String userId, boolean isPrivate) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new RuntimeException("Usuário não encontrado")
        );
        user.setPrivate(isPrivate);
        userRepository.save(user);
    }

    public void toggleFavorite(String userId, String recipeId) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new RuntimeException("Usuário não encontrado")
        );
        if (user.getFavoriteRecipeIds().contains(recipeId)) {
            user.getFavoriteRecipeIds().remove(recipeId);
        } else {
            user.getFavoriteRecipeIds().add(recipeId);
        }
        userRepository.save(user);
    }

    public void addRecipeBookToSaved(String userId, String bookId) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new RuntimeException("Usuário não encontrado")
        );
        if (!user.getSavedBookIds().contains(bookId)) {
            user.getSavedBookIds().add(bookId);
            userRepository.save(user);
        }
    }

    public void removeRecipeBookFromSaved(String userId, String bookId) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new RuntimeException("Usuário não encontrado")
        );
        if (user.getSavedBookIds().contains(bookId)) {
            user.getSavedBookIds().remove(bookId);
            userRepository.save(user);
        }
    }

    public UserPublic getUserProfile(String handle, String currentUserId) {
        User user = userRepository.findByHandle(handle).orElseThrow(
            () -> new RuntimeException("Usuário não encontrado")
        );
        
        // Se o usuário for privado e não for o próprio dono vendo
        if (user.isPrivate() && !user.getId().equals(currentUserId)) {
            throw new RuntimeException("Este perfil é privado");
        }
        
        return new UserPublic(user.getName(), user.getHandle(), user.getBio(), user.getAvatarUrl());
    }

    public UserPublic getUserProfileById(String id, String currentUserId) {
        User user = userRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Usuário não encontrado")
        );
        
        // Se o usuário for privado e não for o próprio dono vendo
        if (user.isPrivate() && !user.getId().equals(currentUserId)) {
            throw new RuntimeException("Este perfil é privado");
        }
        
        return new UserPublic(user.getName(), user.getHandle(), user.getBio(), user.getAvatarUrl());
    }
}
