package com.product.CloudFileStorage.user.service.impl;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.product.CloudFileStorage.common.exceptions.custom.AccessDeniedException;
import com.product.CloudFileStorage.common.exceptions.custom.UserNotFoundException;
import com.product.CloudFileStorage.user.model.entity.User;
import com.product.CloudFileStorage.user.repository.UserRepository;
import com.product.CloudFileStorage.user.security.UserDetailsImpl;
import com.product.CloudFileStorage.user.service.interfaces.SecurityService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of SecurityService.
 * Handles authenticated user retrieval and ownership validation.
 */
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final UserRepository userRepository;

    /**
     * Retrieves the currently authenticated user from the SecurityContext.
     * Validates that the authentication is not null or anonymous.
     */
    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UserNotFoundException("No authenticated user found");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found in database"));
    }

    /**
     * Validates that the currently authenticated user matches the given user ID.
     * Throws AccessDeniedException if the user is not the owner of the resource.
     */
    @Override
    public void validateUser(UUID userId) {
        User currentUser = getCurrentUser();

        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to modify this resource");
        }
    }
}