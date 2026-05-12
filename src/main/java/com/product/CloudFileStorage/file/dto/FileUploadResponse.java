package com.product.CloudFileStorage.file.dto;

import java.util.UUID;

public record FileUploadResponse(
        UUID id,
        String fileName,
        String message
) {
}
