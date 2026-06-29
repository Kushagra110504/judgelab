package com.judgelab.shared;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;

/**
 * Extends {@link BaseEntity} by adding a mutable {@code updated_at} timestamp.
 * <p>
 * Use this superclass for entities that have both {@code created_at} and
 * {@code updated_at} columns (currently: {@code users} and {@code projects}).
 */
@MappedSuperclass
public abstract class AuditableEntity extends BaseEntity {

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    @Override
    protected void prePersist() {
        super.prePersist();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
