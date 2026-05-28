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

        if (updatedUser.getHandle() != null && !updatedUser.getHandle().equals(existing.getHandle())) {
            userRepository.findByHandle(updatedUser.getHandle()).ifPresent(user -> {
                if (!user.getId().equals(userId)) {
                    throw new BusinessException("Handle is already taken", org.springframework.http.HttpStatus.BAD_REQUEST);
                }
            });
        }

        if (updatedUser.getName() != null) {
            existing.setName(updatedUser.getName());
        }
        if (updatedUser.getHandle() != null) {
            existing.setHandle(updatedUser.getHandle());
        }
        if (updatedUser.getAvatarUrl() != null) {
            existing.setAvatarUrl(updatedUser.getAvatarUrl());
        }
        if (updatedUser.getBio() != null) {
            existing.setBio(updatedUser.getBio());
        }
        if (updatedUser.getBirthDate() != null) {
            existing.setBirthDate(updatedUser.getBirthDate());
        }

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
        
        // Se o usuário for privado e não for o próprio dono vendo
        if (user.isPrivate() && !user.getId().equals(currentUserId)) {
            throw new BusinessException("Este perfil é privado", org.springframework.http.HttpStatus.PRECONDITION_FAILED);
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
