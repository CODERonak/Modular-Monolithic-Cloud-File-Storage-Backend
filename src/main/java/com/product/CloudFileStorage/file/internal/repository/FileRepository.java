package com.product.CloudFileStorage.file.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.product.CloudFileStorage.file.internal.model.entity.File;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
   /**
     * Retrieves all files belonging to a specific user.
     * Used to list the authenticated user's own files.
     */
   List<File> findByOwnerId(UUID ownerId);

   /**
    * Checks whether a file with the given storage name exists.
    * Used to prevent duplicate storage file names.
    */
   boolean existsByStorageFileName(String storageFileName);
}
