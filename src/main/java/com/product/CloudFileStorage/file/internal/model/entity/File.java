package com.product.CloudFileStorage.file.internal.model.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    private UUID folderId;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storageFileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String fileUrl;

    private Instant uploadedAt;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = Instant.now();
    }
}
