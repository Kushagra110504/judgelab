package com.judgelab.auth;

import com.judgelab.shared.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * JudgeLab platform user.
 * <p>
 * Owns one or more {@link com.judgelab.project.Project} records.
 * The {@code email} column carries a unique constraint defined in the V1 migration.
 */
@Entity
@Table(name = "users")
public class User extends AuditableEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    protected User() {
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
