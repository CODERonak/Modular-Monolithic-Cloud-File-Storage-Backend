package com.product.CloudFileStorage.user.internal.dto;

import com.product.CloudFileStorage.user.internal.model.enums.Role;

public record RegisterResponse(
        String email,
        String fullname,
        Role role) {
}
