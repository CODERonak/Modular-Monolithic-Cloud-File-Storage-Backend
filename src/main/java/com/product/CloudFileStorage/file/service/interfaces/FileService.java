package com.product.CloudFileStorage.file.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.product.CloudFileStorage.file.dto.*;
import com.product.CloudFileStorage.user.model.entity.User;

public interface FileService {
    FileUploadResponse uploadFile(FileUploadRequest fileUploadRequest, User owner);

    FileResponse getFileById(UUID id, User owner);

    void deleteFile(UUID id, User owner);

    FileMetadataResponse getFileMetadataById(UUID id, User owner);

    List<FileMetadataResponse> getAllFiles(User owner);
}