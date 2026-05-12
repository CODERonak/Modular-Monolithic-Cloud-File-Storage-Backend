package com.product.CloudFileStorage.file.dto;

import java.time.Instant;

public record FileMetadataResponse(
                String fileName,
                String contentType,
                long fileSize,
                Instant uploadAt) {
}
