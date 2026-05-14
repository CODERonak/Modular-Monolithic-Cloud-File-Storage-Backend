package com.product.CloudFileStorage.file.internal.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.product.CloudFileStorage.file.internal.dto.*;
import com.product.CloudFileStorage.file.internal.exception.*;
import com.product.CloudFileStorage.file.internal.mapper.FileMapper;
import com.product.CloudFileStorage.file.internal.model.entity.File;
import com.product.CloudFileStorage.file.internal.repository.FileRepository;
import com.product.CloudFileStorage.file.internal.service.interfaces.FileService;
import com.product.CloudFileStorage.user.api.UserModuleAPI;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all file operations including GCS interactions and database
 * persistence.
 * USER operations are scoped to the authenticated user via JWT.
 * ADMIN operations are metadata only — no file content or signed URLs exposed.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private static final long MAX_FILE_SIZE = 40 * 1024 * 1024; // 40MB in bytes

    private final Storage gcsStorage;
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;
    private final UserModuleAPI userModuleAPI;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    /**
     * Uploads a file to GCS and saves its metadata to the database.
     * Validates file size and content type before uploading.
     * File is stored under users/{ownerId}/{storageFileName} in GCS.
     */

    @Override
    public FileUploadResponse uploadFile(FileUploadRequest fileUploadRequest) {
        UUID ownerId = userModuleAPI.getCurrentUser().getId();

        // Validate file size
        if (fileUploadRequest.getFile().getSize() > MAX_FILE_SIZE) {
            throw new FileSizeLimitExceededException("File size exceeds the 40MB limit");
        }

        // Validate content type
        String contentType = fileUploadRequest.getFile().getContentType();
        if (contentType == null || contentType.isBlank()) {
            throw new InvalidFileTypeException("Invalid or missing file format");
        }

        String originalFileName = fileUploadRequest.getFile().getOriginalFilename();
        String storageFileName = UUID.randomUUID() + "_" + originalFileName;
        String storagePath = "users/" + ownerId + "/" + storageFileName;

        try {
            BlobId blobId = BlobId.of(bucketName, storagePath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(contentType)
                    .build();

            // Upload file bytes to GCS
            gcsStorage.create(blobInfo, fileUploadRequest.getFile().getBytes());

            log.info("File uploaded to GCS: {}", storagePath);

        } catch (IOException | StorageException e) {
            log.error("Failed to upload file to GCS: {}", e.getMessage());
            throw new FileUploadException("Failed to upload file to storage");
        }

        File file = new File();
        file.setOwnerId(ownerId);
        file.setOriginalFileName(originalFileName);
        file.setStorageFileName(storageFileName);
        file.setContentType(contentType);
        file.setSize(fileUploadRequest.getFile().getSize());
        file.setFileUrl(storagePath);

        File savedFile = fileRepository.save(file);
        log.info("File metadata saved to database with ID: {}", savedFile.getId());

        return fileMapper.toUploadResponse(savedFile);

    }

    /**
     * Generates a signed GCS URL for downloading a specific file.
     * Validates ownership before generating the URL.
     * Signed URL is valid for 15 minutes.
     */

    @Override
    public FileResponse getDownloadLinkByFileId(UUID fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + fileId));

        // Validate ownership using ownerId directly
        userModuleAPI.validateUser(file.getOwnerId());

        String signedUrl = generateSignedUrl(file.getFileUrl());

        return new FileResponse(signedUrl, file.getContentType(), file.getUploadedAt());
    }

    /**
     * Deletes a file from GCS and removes its metadata from the database.
     * Validates ownership before deletion.
     * GCS deletion happens before database deletion — if GCS fails, DB record is
     * preserved.
     */

    @Override
    public void deleteFile(UUID fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + fileId));

        // Validate ownership using ownerId directly
        userModuleAPI.validateUser(file.getOwnerId());

        try {
            BlobId blobId = BlobId.of(bucketName, file.getFileUrl());
            gcsStorage.delete(blobId);

            log.info("File deleted from GCS: {}", file.getFileUrl());

        } catch (StorageException e) {
            log.error("Failed to delete file from GCS: {}", e.getMessage());
            throw new StorageException("Failed to delete file from storage");
        }

        // Delete metadata from database after GCS deletion
        fileRepository.deleteById(fileId);
        log.info("File metadata deleted from database for ID: {}", fileId);
    }

    /**
     * Retrieves all files belonging to the authenticated user.
     * Generates a signed GCS URL for each file valid for 15 minutes.
     * Scoped strictly to the calling user via JWT — no cross-user access possible.
     */

    @Override
    public List<UserFileResponse> getMyFiles() {
        UUID ownerId = userModuleAPI.getCurrentUser().getId();

        List<File> files = fileRepository.findByOwnerId(ownerId);

        return files.stream()
                .map(file -> {
                    String signedUrl = generateSignedUrl(file.getFileUrl());
                    return new UserFileResponse(
                            file.getId(),
                            file.getOriginalFileName(),
                            file.getContentType(),
                            file.getSize(),
                            signedUrl);
                })
                .toList();
    }

    /**
     * Retrieves metadata for all files belonging to a specific user.
     * Admin only — accepts explicit userId parameter.
     * Returns metadata only — no signed URLs generated.
     */

    @Override
    public FileMetadataResponse getFileMetadataById(UUID fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + fileId));

        return fileMapper.toMetadataResponse(file);
    }

    /**
     * Retrieves metadata for all files belonging to a specific user.
     * Admin only — accepts explicit userId parameter.
     * Returns metadata only — no signed URLs generated.
     */

    @Override
    public List<FileMetadataResponse> getAllFilesMetadataByUserId(UUID userId) {
        List<File> files = fileRepository.findByOwnerId(userId);
        return files.stream()
                .map(fileMapper::toMetadataResponse)
                .toList();
    }

    /**
     * Generates a signed GCS URL for a given storage path.
     * URL is valid for 15 minutes after generation.
     * Used internally by download and getMyFiles operations.
     */

    private String generateSignedUrl(String fileUrl) {
        try {
            BlobId blobId = BlobId.of(bucketName, fileUrl);
            return gcsStorage.signUrl(
                    BlobInfo.newBuilder(blobId).build(),
                    15,
                    TimeUnit.MINUTES,
                    Storage.SignUrlOption.withV4Signature())
                    .toString();
        } catch (StorageException e) {
            log.error("Failed to generate signed URL for file URL {}: {}", fileUrl, e.getMessage());
            throw new FileDownloadException("Failed to generate download URL");
        }
    }

}
