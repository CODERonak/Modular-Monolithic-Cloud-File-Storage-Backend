package com.product.CloudFileStorage.file.internal.dto;

import java.util.UUID;

public record FileUploadResponse(
        UUID id,
        String fileName,
        String message
) {
}
