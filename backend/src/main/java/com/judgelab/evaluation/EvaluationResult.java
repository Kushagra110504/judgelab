package com.judgelab.evaluation;

import com.judgelab.experiment.Experiment;
import com.judgelab.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Aggregated evaluation output for a completed {@link Experiment}.
 * <p>
 * All score columns are nullable — an experiment may complete without a
 * specific scorer being configured. {@link BigDecimal} with NUMERIC(5,4)
 * preserves four decimal places (e.g. 0.9231) without floating-point drift.
 * {@code rawMetrics} captures the full evaluation payload for auditability.
 */
@Entity
@Table(name = "evaluation_results")
public class EvaluationResult extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "experiment_id", nullable = false, unique = true)
    private Experiment experiment;

    @Column(precision = 5, scale = 4)
    private BigDecimal overallScore;

    @Column(precision = 5, scale = 4)
    private BigDecimal qualityScore;

    @Column(precision = 5, scale = 4)
    private BigDecimal latencyScore;

    @Column(precision = 5, scale = 4)
    private BigDecimal hallucinationScore;

    @Column(precision = 5, scale = 4)
    private BigDecimal costScore;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> rawMetrics;

    protected EvaluationResult() {
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public BigDecimal getOverallScore() {
        return overallScore;
    }

    public BigDecimal getQualityScore() {
        return qualityScore;
    }

    public BigDecimal getLatencyScore() {
        return latencyScore;
    }

    public BigDecimal getHallucinationScore() {
        return hallucinationScore;
    }

    public BigDecimal getCostScore() {
        return costScore;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public Map<String, Object> getRawMetrics() {
        return rawMetrics;
    }
}
