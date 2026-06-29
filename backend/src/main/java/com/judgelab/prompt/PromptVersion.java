package com.judgelab.prompt;

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
 * An immutable, versioned snapshot of a prompt template.
 * <p>
 * {@code importedFile} is nullable — prompts can be written in-app
 * without requiring a file upload.
 */
@Entity
@Table(name = "prompt_versions")
public class PromptVersion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imported_file_id")
    private ImportedFile importedFile;

    @Column(nullable = false)
    private Integer versionNumber;

    /**
     * Short human-readable display label for the UI (e.g. "baseline", "few-shot", "cot-v2").
     * Optional — {@code version_number} is the canonical identifier.
     */
    @Column(length = 100)
    private String versionName;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String promptText;

    protected PromptVersion() {
    }

    public Project getProject() {
        return project;
    }

    public ImportedFile getImportedFile() {
        return importedFile;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getName() {
        return name;
    }

    public String getPromptText() {
        return promptText;
    }
}
