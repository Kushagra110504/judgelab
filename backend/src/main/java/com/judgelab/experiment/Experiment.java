package com.judgelab.experiment;

import com.judgelab.dataset.Dataset;
import com.judgelab.evaluation.EvaluationConfiguration;
import com.judgelab.project.ModelConfiguration;
import com.judgelab.project.Project;
import com.judgelab.prompt.PromptVersion;
import com.judgelab.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * A single experiment run: one prompt version × one dataset × one model
 * configuration × one evaluation configuration.
 * <p>
 * Foreign keys to prompt, dataset, model, and evaluation configurations use
 * {@code ON DELETE RESTRICT} (enforced in SQL) to prevent accidental deletion
 * of resources referenced by existing experiments.
 */
@Entity
@Table(name = "experiments")
public class Experiment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prompt_version_id", nullable = false)
    private PromptVersion promptVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dataset_id", nullable = false)
    private Dataset dataset;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_configuration_id", nullable = false)
    private ModelConfiguration modelConfiguration;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evaluation_configuration_id", nullable = false)
    private EvaluationConfiguration evaluationConfiguration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ExperimentStatus status = ExperimentStatus.PENDING;

    @Column
    private Instant startedAt;

    @Column
    private Instant completedAt;

    protected Experiment() {
    }

    public Project getProject() {
        return project;
    }

    public PromptVersion getPromptVersion() {
        return promptVersion;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public ModelConfiguration getModelConfiguration() {
        return modelConfiguration;
    }

    public EvaluationConfiguration getEvaluationConfiguration() {
        return evaluationConfiguration;
    }

    public ExperimentStatus getStatus() {
        return status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
