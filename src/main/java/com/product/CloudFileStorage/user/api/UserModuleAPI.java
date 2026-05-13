package com.product.CloudFileStorage.user.api;

import java.util.UUID;

import com.product.CloudFileStorage.user.internal.model.entity.User;

public interface UserModuleAPI {
    User getCurrentUser();

    void validateUser(UUID userId);
}
