package com.product.CloudFileStorage.file.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.product.CloudFileStorage.file.model.entity.File;
import com.product.CloudFileStorage.user.internal.model.entity.User;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    /**
     * Retrieves all files belonging to a specific user.
     * Used to list the authenticated user's own files.
     */
    List<File> findByOwner(User owner);

    /**
     * Checks whether a file with the given storage name exists.
     * Used to prevent duplicate storage file names.
     */
    boolean existsByStorageFileName(String storageFileName);
}
