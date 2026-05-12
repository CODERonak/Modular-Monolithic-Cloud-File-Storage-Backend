package com.product.CloudFileStorage.file.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.product.CloudFileStorage.file.dto.FileMetadataResponse;
import com.product.CloudFileStorage.file.dto.FileUploadResponse;
import com.product.CloudFileStorage.file.model.entity.File;

@Mapper(componentModel = "spring")
public interface FileMapper {

    // Converts a File entity to a FileMetadataResponse DTO.
    // Maps entity fields to metadata response for admin or user file info.
    @Mapping(target = "fileName", source = "originalFileName")
    @Mapping(target = "uploadAt", source = "uploadedAt")
    @Mapping(target = "fileSize", source = "size")
    FileMetadataResponse toMetadataResponse(File file);

    // Converts a File entity to a FileUploadResponse DTO.
    // Returns the original file name and a success message.
    @Mapping(target = "fileName", source = "originalFileName")
    @Mapping(target = "message", constant = "File uploaded successfully")
    @Mapping(target = "id", source = "id")
    FileUploadResponse toUploadResponse(File file);
}
