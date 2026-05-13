package com.product.CloudFileStorage.file.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.product.CloudFileStorage.file.dto.*;
import com.product.CloudFileStorage.file.service.interfaces.FileService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Operation(summary = "Upload a file on GCP storage bucket")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestPart("file") MultipartFile file) {
        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFile(file);
        var response = fileService.uploadFile(fileUploadRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Download a file from GCP storage bucket")
    @GetMapping("/download/{id}")
    public ResponseEntity<FileResponse> downloadFile(@PathVariable UUID id) {
        var response = fileService.getFileById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete a file from GCP storage bucket")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable UUID id) {
        fileService.deleteFile(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get a file metadata by id from GCP storage bucketand is a admin only endpoint")
    @GetMapping("/metadata/{id}")
    public ResponseEntity<FileMetadataResponse> getFileMetadata(@PathVariable UUID id) {
        var response = fileService.getFileMetadataById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all files metadata by user id from GCP storage bucket and is a admin only endpoint")
    @GetMapping("/metadata/all/{id}")
    public ResponseEntity<List<FileMetadataResponse>> getAllFiles(@PathVariable UUID id) {
        var response = fileService.getAllFiles(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
