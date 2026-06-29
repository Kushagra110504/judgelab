package com.judgelab.project;

import com.judgelab.auth.User;
import com.judgelab.shared.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A judge project owned by a {@link User}.
 * <p>
 * Projects act as the top-level organisational unit for imported files,
 * prompt versions, datasets, model configurations, and experiments.
 */
@Entity
@Table(name = "projects")
public class Project extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    protected Project() {
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
