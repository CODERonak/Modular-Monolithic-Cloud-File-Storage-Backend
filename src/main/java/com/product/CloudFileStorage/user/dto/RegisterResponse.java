package com.product.CloudFileStorage.user.dto;

import javax.management.relation.Role;

public record RegisterResponse(
        String email,
        String fullname,
        Role role) {
}
