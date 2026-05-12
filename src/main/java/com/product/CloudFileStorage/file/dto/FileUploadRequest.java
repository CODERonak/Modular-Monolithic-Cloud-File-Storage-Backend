package com.product.CloudFileStorage.file.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FileUploadRequest {

    @NotNull(message = "File is required")
    private MultipartFile file;
}
