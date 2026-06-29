package com.judgelab.project;

import com.judgelab.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * LLM model configuration snapshot for a project.
 * <p>
 * Temperature and top_p use {@link BigDecimal} to preserve exact decimal values
 * without floating-point rounding errors.
 */
@Entity
@Table(name = "model_configurations")
public class ModelConfiguration extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 100)
    private String provider;

    @Column(nullable = false)
    private String modelName;

    @Column(precision = 4, scale = 3)
    private BigDecimal temperature;

    @Column(name = "top_p", precision = 4, scale = 3)
    private BigDecimal topP;

    @Column
    private Integer maxTokens;

    protected ModelConfiguration() {
    }

    public Project getProject() {
        return project;
    }

    public String getProvider() {
        return provider;
    }

    public String getModelName() {
        return modelName;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public BigDecimal getTopP() {
        return topP;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }
}
