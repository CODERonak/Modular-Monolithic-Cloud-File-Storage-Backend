package com.product.CloudFileStorage.file.internal.service.interfaces;

import com.product.CloudFileStorage.file.internal.dto.FileMetadataResponse;
import com.product.CloudFileStorage.file.internal.dto.FileResponse;
import com.product.CloudFileStorage.file.internal.dto.FileUploadRequest;
import com.product.CloudFileStorage.file.internal.dto.FileUploadResponse;
import com.product.CloudFileStorage.file.internal.dto.UserFileResponse;

import java.util.List;
import java.util.UUID;

public interface FileService {
    FileUploadResponse uploadFile(FileUploadRequest fileUploadRequest);

    FileResponse getDownloadLinkByFileId(UUID fileId);

    void deleteFile(UUID fileId);

    List<UserFileResponse> getMyFiles();

    FileMetadataResponse getFileMetadataById(UUID fileId);

    List<FileMetadataResponse> getAllFilesMetadataByUserId(UUID userId);

}
