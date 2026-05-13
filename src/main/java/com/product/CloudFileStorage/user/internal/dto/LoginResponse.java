package com.product.CloudFileStorage.user.internal.dto;

public record LoginResponse(
        String email,
        String fullname,
        String token) {
}
