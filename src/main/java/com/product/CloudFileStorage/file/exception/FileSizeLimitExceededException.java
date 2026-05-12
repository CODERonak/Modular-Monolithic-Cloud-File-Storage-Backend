package com.product.CloudFileStorage.file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONTENT_TOO_LARGE)
public class FileSizeLimitExceededException extends RuntimeException {
    public FileSizeLimitExceededException(String message) {
        super(message);
    }
}
