package com.product.CloudFileStorage.file.internal.dto;

import java.time.Instant;

public record FileResponse(
        String signedUrl,    // pre-signed GCS URL
        String contentType,
        Instant expiresAt  
) {
}
