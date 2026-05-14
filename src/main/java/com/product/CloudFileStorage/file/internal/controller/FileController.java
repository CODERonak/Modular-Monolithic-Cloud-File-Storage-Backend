package com.product.CloudFileStorage.file.internal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.product.CloudFileStorage.file.internal.dto.*;
import com.product.CloudFileStorage.file.internal.service.interfaces.FileService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

/**
 * Handles file operations for the Cloud File Storage system.
 * USER endpoints manage personal file operations.
 * ADMIN endpoints provide metadata access without file content visibility.
 */

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    /**
     * Uploads a file to GCS storage under the authenticated user's directory.
     * Only accessible by authenticated users.
     */

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Upload a file on GCP storage bucket")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestPart("file") MultipartFile file) {
        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFile(file);
        var response = fileService.uploadFile(fileUploadRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Generates a signed GCS URL for downloading a specific file.
     * Validates ownership before generating the URL.
     * Only accessible by the file owner.
     */

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Download a file from GCP storage bucket")
    @GetMapping("/download/{fileId}")
    public ResponseEntity<FileResponse> downloadFile(@PathVariable UUID fileId) {
        var response = fileService.getDownloadLinkByFileId(fileId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deletes a file from GCS and removes its metadata from the database.
     * Validates ownership before deletion.
     * Only accessible by the file owner.
     */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Delete a file from GCP storage bucket")
    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable UUID fileId) {
        fileService.deleteFile(fileId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Retrieves all files belonging to the authenticated user.
     * Each file includes a signed GCS URL valid for 15 minutes.
     * Scoped strictly to the calling user — no cross-user or admin access.
     */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Get all files of the user from GCP storage bucket")
    @GetMapping("/my-files")
    public ResponseEntity<List<UserFileResponse>> getMyFiles() {
        var response = fileService.getMyFiles();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves metadata for a specific file by file ID.
     * Returns metadata only — no file content or signed URLs exposed.
     * Admin only endpoint.
     */

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get a file metadata by id from GCP storage bucketand is a admin only endpoint")
    @GetMapping("/metadata/{fileId}")
    public ResponseEntity<FileMetadataResponse> getFileMetadata(@PathVariable UUID fileId) {
        var response = fileService.getFileMetadataById(fileId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves metadata for all files belonging to a specific user.
     * Returns metadata only — no file content or signed URLs exposed.
     * Admin only endpoint.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all files metadata by user id from GCP storage bucket and is a admin only endpoint")
    @GetMapping("/metadata/all/{userId}")
    public ResponseEntity<List<FileMetadataResponse>> getAllFiles(@PathVariable UUID userId) {
        var response = fileService.getAllFilesMetadataByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
