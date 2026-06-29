package com.judgelab.dataset;

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
 * A single input/expected-output pair belonging to a {@link Dataset}.
 * <p>
 * {@code metadata} is stored as JSONB for flexible, schema-free annotations
 * (e.g. difficulty level, category tags, source reference).
 */
@Entity
@Table(name = "test_cases")
public class TestCase extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dataset_id", nullable = false)
    private Dataset dataset;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String input;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String expectedOutput;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    protected TestCase() {
    }

    public Dataset getDataset() {
        return dataset;
    }

    public String getInput() {
        return input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
