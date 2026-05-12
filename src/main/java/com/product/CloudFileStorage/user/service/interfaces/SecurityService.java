package com.product.CloudFileStorage.user.service.interfaces;

import com.product.CloudFileStorage.user.model.entity.User;
import java.util.UUID;

public interface SecurityService {
    User getCurrentUser();
 
    void validateUser(UUID userId);
 }
