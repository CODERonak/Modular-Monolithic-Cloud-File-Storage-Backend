package com.product.CloudFileStorage.file.internal.dto;

import java.util.UUID;

public record UserFileResponse(
        UUID id,
        String fileName,
        String contentType,
        Long size,
        String fileUrl
) {}