package com.judgelab.evaluation;

import com.judgelab.project.Project;
import com.judgelab.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * A named evaluation strategy stored as JSONB.
 * <p>
 * JSONB is used here because evaluation strategies (scorers, weights,
 * thresholds) are expected to evolve rapidly and do not warrant a rigid schema.
 */
@Entity
@Table(name = "evaluation_configurations")
public class EvaluationConfiguration extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> configuration;

    protected EvaluationConfiguration() {
    }

    public Project getProject() {
        return project;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }
}
