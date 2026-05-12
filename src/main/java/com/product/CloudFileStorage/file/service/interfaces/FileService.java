package com.product.CloudFileStorage.file.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.product.CloudFileStorage.file.dto.*;

public interface FileService {
    FileUploadResponse uploadFile(FileUploadRequest fileUploadRequest);

    FileResponse getFileById(UUID id);

    void deleteFile(UUID id);

    FileMetadataResponse getFileMetadataById(UUID id);

    List<FileMetadataResponse> getAllFiles();
}
