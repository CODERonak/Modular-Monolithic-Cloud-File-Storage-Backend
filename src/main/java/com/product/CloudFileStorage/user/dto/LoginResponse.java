package com.product.CloudFileStorage.user.dto;

public record LoginResponse(
        String email,
        String fullname,
        String token) {
}
