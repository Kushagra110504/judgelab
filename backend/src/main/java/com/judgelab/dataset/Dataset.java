package com.judgelab.dataset;

import com.judgelab.project.ImportedFile;
import com.judgelab.project.Project;
import com.judgelab.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A named collection of {@link TestCase} records.
 * <p>
 * {@code importedFile} is nullable — datasets can be built manually
 * or seeded from an uploaded file.
 */
@Entity
@Table(name = "datasets")
public class Dataset extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imported_file_id")
    private ImportedFile importedFile;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    protected Dataset() {
    }

    public Project getProject() {
        return project;
    }

    public ImportedFile getImportedFile() {
        return importedFile;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
