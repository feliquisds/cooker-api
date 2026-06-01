package com.expsn.cooker.service;

import org.springframework.stereotype.Service;

import com.expsn.cooker.exception.BusinessException;
import com.expsn.cooker.exception.ItemException;
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
            () -> new ItemException("Usuário não encontrado")
        );
    }

    public User save(User updatedUser, String userId) {
        User existing = findById(userId);

        if (updatedUser.getHandle() != existing.getHandle()) {
            userRepository.findByHandle(updatedUser.getHandle()).ifPresent(user -> {
                throw new BusinessException("Nome de usuário já cadastrado");
            });
            existing.setHandle(updatedUser.getHandle());
        }
        if (updatedUser.getEmail() != existing.getEmail()) {
            userRepository.findByEmail(updatedUser.getEmail()).ifPresent(user -> {
                throw new BusinessException("Email já cadastrado");
            });
            existing.setEmail(updatedUser.getEmail());
        }

        existing.setName(updatedUser.getName());
        existing.setHandle(updatedUser.getHandle());
        existing.setAvatarUrl(updatedUser.getAvatarUrl());
        existing.setBio(updatedUser.getBio());
        existing.setBirthDate(updatedUser.getBirthDate());
        existing.setRequestNotificationTags(updatedUser.getRequestNotificationTags());
        existing.setRecipeNotificationTags(updatedUser.getRecipeNotificationTags());
        existing.setPrivate(updatedUser.isPrivate());

        return userRepository.save(existing);
    }

    public void updatePrivacy(String userId, boolean isPrivate) {
        User user = findById(userId);
        user.setPrivate(isPrivate);
        userRepository.save(user);
    }

    public void toggleFavorite(String userId, String recipeId) {
        User user = findById(userId);
        if (user.getFavoriteRecipeIds() == null) {
            user.setFavoriteRecipeIds(new java.util.ArrayList<>());
        }
        if (user.getFavoriteRecipeIds().contains(recipeId)) {
            user.getFavoriteRecipeIds().remove(recipeId);
        } else {
            user.getFavoriteRecipeIds().add(recipeId);
        }
        userRepository.save(user);
    }

    public void addRecipeBookToSaved(String userId, String bookId) {
        User user = findById(userId);
        if (user.getSavedBookIds() == null) {
            user.setSavedBookIds(new java.util.ArrayList<>());
        }
        if (!user.getSavedBookIds().contains(bookId)) {
            user.getSavedBookIds().add(bookId);
            userRepository.save(user);
        }
    }

    public void removeRecipeBookFromSaved(String userId, String bookId) {
        User user = findById(userId);
        if (user.getSavedBookIds() == null) {
            return;
        }
        if (user.getSavedBookIds().contains(bookId)) {
            user.getSavedBookIds().remove(bookId);
            userRepository.save(user);
        }
    }

    public UserPublic getUserProfile(String handle, String currentUserId) {
        User user = userRepository.findByHandle(handle).orElseThrow(
            () -> new ItemException("Usuário não encontrado")
        );
        
        if (user.isPrivate() && !user.getId().equals(currentUserId)) {
            throw new BusinessException("Este perfil é privado");
        }
        
        return new UserPublic(user.getName(), user.getHandle(), user.getBio(), user.getAvatarUrl());
    }

    public UserPublic getUserProfileById(String id, String currentUserId) {
        User user = findById(id);
        
        // Se o usuário for privado e não for o próprio dono vendo
        if (user.isPrivate() && !user.getId().equals(currentUserId)) {
            throw new BusinessException("Este perfil é privado", org.springframework.http.HttpStatus.PRECONDITION_FAILED);
        }
        
        return new UserPublic(user.getName(), user.getHandle(), user.getBio(), user.getAvatarUrl());
    }
}
