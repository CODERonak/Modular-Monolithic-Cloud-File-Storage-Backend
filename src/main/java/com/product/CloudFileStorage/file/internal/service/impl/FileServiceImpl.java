package com.product.CloudFileStorage.file.internal.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.product.CloudFileStorage.common.exceptions.custom.UserNotFoundException;
import com.product.CloudFileStorage.file.internal.dto.*;
import com.product.CloudFileStorage.file.internal.exception.*;
import com.product.CloudFileStorage.file.internal.mapper.FileMapper;
import com.product.CloudFileStorage.file.internal.model.entity.File;
import com.product.CloudFileStorage.file.internal.repository.FileRepository;
import com.product.CloudFileStorage.file.internal.service.interfaces.FileService;
import com.product.CloudFileStorage.user.api.UserModuleAPI;
import com.product.CloudFileStorage.user.internal.model.entity.User;
import com.product.CloudFileStorage.user.internal.repository.UserRepository;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private static final long MAX_FILE_SIZE = 40 * 1024 * 1024; // 40MB in bytes

    private final Storage gcsStorage;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;
    private final UserModuleAPI userModuleAPI;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    // Uploads a file to GCS and saves its metadata to the database.
    // Validates file size before uploading.
    @Override
    public FileUploadResponse uploadFile(FileUploadRequest fileUploadRequest) {
        User owner = userModuleAPI.getCurrentUser();

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
        String storagePath = "users/" + owner.getId() + "/" + storageFileName;

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
        file.setOwner(owner);
        file.setOriginalFileName(originalFileName);
        file.setStorageFileName(storageFileName);
        file.setContentType(contentType);
        file.setSize(fileUploadRequest.getFile().getSize());
        file.setFileUrl(storagePath);

        File savedFile = fileRepository.save(file);
        log.info("File metadata saved to database with ID: {}", savedFile.getId());

        return fileMapper.toUploadResponse(savedFile);

    }

    @Override
    public FileResponse getFileById(UUID id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + id));

        // Validate ownership
        userModuleAPI.validateUser(file.getOwner().getId());

        try {
            BlobId blobId = BlobId.of(bucketName, file.getFileUrl());

            // Generate signed URL valid for 15 minutes
            String signedUrl = gcsStorage.signUrl(
                    BlobInfo.newBuilder(blobId).build(),
                    15,
                    TimeUnit.MINUTES,
                    Storage.SignUrlOption.withV4Signature())
                    .toString();

            log.info("Signed URL generated for file ID: {}", id);

            return new FileResponse(signedUrl, file.getContentType(), file.getUploadedAt());

        } catch (StorageException e) {
            log.error("Failed to generate signed URL for file ID {}: {}", id, e.getMessage());
            throw new FileDownloadException("Failed to generate download URL");
        }
    }

    @Override
    public void deleteFile(UUID id) {
        fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + id));

        File file = fileRepository.findById(id).get();

        // Validate ownership
        userModuleAPI.validateUser(file.getOwner().getId());

        try {
            BlobId blobId = BlobId.of(bucketName, file.getFileUrl());
            gcsStorage.delete(blobId);

            log.info("File deleted from GCS: {}", file.getFileUrl());

        } catch (StorageException e) {
            log.error("Failed to delete file from GCS: {}", e.getMessage());
            throw new StorageException(
                    "Failed to delete file from storage");
        }

        // Delete metadata from database after GCS deletion
        fileRepository.deleteById(id);
        log.info("File metadata deleted from database for ID: {}", id);
    }

    @Override
    public FileMetadataResponse getFileMetadataById(UUID id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + id));

        return fileMapper.toMetadataResponse(file);
    }

    @Override
    public List<FileMetadataResponse> getAllFiles(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        List<File> files = fileRepository.findByOwner(user);
        return files.stream().map(fileMapper::toMetadataResponse).toList();
    }

}
