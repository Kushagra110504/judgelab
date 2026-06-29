package com.judgelab.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * A file imported into a project (CSV, JSONL, plain text, etc.).
 * <p>
 * {@code ImportedFile} does not extend {@link com.judgelab.shared.BaseEntity}
 * because its timestamp column is named {@code imported_at}, not {@code created_at}.
 * The checksum (SHA-256 hex) allows deduplication at the service layer.
 */
@Entity
@Table(name = "imported_files")
public class ImportedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String relativePath;

    @Column(nullable = false, length = 100)
    private String fileType;

    @Column(nullable = false, length = 64)
    private String checksum;

    /** File size in bytes — used to detect changes during folder synchronisation. */
    @Column
    private Long fileSize;

    /** Wall-clock modification time of the source file — used for change detection. */
    @Column
    private Instant lastModified;

    @Column(nullable = false, updatable = false)
    private Instant importedAt;

    protected ImportedFile() {
    }

    @PrePersist
    protected void prePersist() {
        this.importedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public String getFilename() {
        return filename;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getFileType() {
        return fileType;
    }

    public String getChecksum() {
        return checksum;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public Instant getImportedAt() {
        return importedAt;
    }
}
