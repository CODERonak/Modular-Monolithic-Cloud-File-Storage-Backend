package com.product.CloudFileStorage.user.internal.service.interfaces;

import com.product.CloudFileStorage.user.internal.model.entity.User;
import java.util.UUID;

public interface SecurityService {
    User getCurrentUser();
    
    void validateUser(UUID userId);
}