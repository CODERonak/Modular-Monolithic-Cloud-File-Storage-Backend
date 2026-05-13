package com.product.CloudFileStorage.user.api;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.product.CloudFileStorage.user.internal.model.entity.User;
import com.product.CloudFileStorage.user.internal.service.interfaces.SecurityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserModuleAPIImpl implements UserModuleAPI {
    private final SecurityService securityService;

    @Override
    public User getCurrentUser() {
        return securityService.getCurrentUser();
    }

    @Override
    public void validateUser(UUID userId) {
        securityService.validateUser(userId);
    }
}
