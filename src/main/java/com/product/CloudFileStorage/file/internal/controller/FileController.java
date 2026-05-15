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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Handles file operations for the Cloud File Storage system.
 * USER endpoints manage personal file operations.
 * ADMIN endpoints provide metadata access without file content visibility.
 */

@Tag(name = "File Management", description = "Endpoints for uploading, downloading, deleting and managing files in GCS. "
        +
        "USER endpoints are scoped to the authenticated user. " +
        "ADMIN endpoints provide metadata access only.")

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    /**
     * Uploads a file to GCS storage under the authenticated user's directory.
     * Only accessible by authenticated users.
     */

    @Operation(summary = "Upload a file", description = "Uploads a file to GCS under the authenticated user's directory. "
            +
            "Maximum file size is 40MB. All file types are supported.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "413", description = "File size exceeds 40MB limit"),
            @ApiResponse(responseCode = "415", description = "Invalid or missing file format"),
            @ApiResponse(responseCode = "500", description = "Failed to upload file to storage")
    })
    @PreAuthorize("hasAuthority('ROLE_USER')")
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

    @Operation(summary = "Download a file", description = "Generates a signed GCS URL valid for 15 minutes for downloading a specific file. "
            +
            "Only the file owner can access this endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Signed URL generated successfully"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "403", description = "Access denied — not the file owner"),
            @ApiResponse(responseCode = "500", description = "Failed to generate download URL")
    })
    @PreAuthorize("hasAuthority('ROLE_USER')")
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

    @Operation(summary = "Delete a file", description = "Deletes a file from GCS and removes its metadata from the database. "
            +
            "Only the file owner can delete their own files.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "File deleted successfully"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "403", description = "Access denied — not the file owner"),
            @ApiResponse(responseCode = "500", description = "Failed to delete file from storage")
    })
    @PreAuthorize("hasAuthority('ROLE_USER')")
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

    @Operation(summary = "Get my files", description = "Retrieves all files belonging to the authenticated user. " +
            "Each file includes a signed GCS URL valid for 15 minutes. " +
            "Strictly scoped to the calling user — no admin or cross-user access.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Files retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PreAuthorize("hasAuthority('ROLE_USER')")
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

    @Operation(summary = "Get file metadata by ID — Admin only", description = "Retrieves metadata for a specific file by file ID. "
            +
            "Returns metadata only — no file content or signed URLs are exposed. " +
            "Admin only endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File metadata retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "403", description = "Access denied — admin only")
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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

    @Operation(summary = "Get all files metadata by user ID — Admin only", description = "Retrieves metadata for all files belonging to a specific user. "
            +
            "Returns metadata only — no file content or signed URLs are exposed. " +
            "Admin only endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Files metadata retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — admin only")
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/metadata/all/{userId}")
    public ResponseEntity<List<FileMetadataResponse>> getAllFiles(@PathVariable UUID userId) {
        var response = fileService.getAllFilesMetadataByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
