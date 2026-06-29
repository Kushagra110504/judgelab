-- ============================================================
-- V1__initial_schema.sql
-- JudgeLab initial database schema
-- ============================================================

-- ─── Users ───────────────────────────────────────────────────
-- Central identity table; owns projects via CASCADE.
CREATE TABLE users (
    id              UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255)             NOT NULL,
    email           VARCHAR(255)             NOT NULL,
    password_hash   VARCHAR(255)             NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uq_users_email UNIQUE (email)
);

-- ─── Projects ────────────────────────────────────────────────
CREATE TABLE projects (
    id          UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID                     NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(255)             NOT NULL,
    description TEXT,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_projects_user_id ON projects(user_id);

-- ─── Imported Files ──────────────────────────────────────────
-- Files uploaded by a user into a project (CSV, JSONL, etc.).
-- Used optionally by datasets and prompt_versions.
CREATE TABLE imported_files (
    id            UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id    UUID                     NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    filename      VARCHAR(255)             NOT NULL,
    relative_path TEXT                     NOT NULL,
    file_type     VARCHAR(100)             NOT NULL,
    checksum      VARCHAR(64)              NOT NULL,
    imported_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_imported_files_project_id ON imported_files(project_id);

-- ─── Prompt Versions ─────────────────────────────────────────
-- Versioned prompt templates; optionally imported from a file.
CREATE TABLE prompt_versions (
    id               UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id       UUID                     NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    imported_file_id UUID                     REFERENCES imported_files(id) ON DELETE SET NULL,
    version_number   INTEGER                  NOT NULL,
    name             VARCHAR(255)             NOT NULL,
    prompt_text      TEXT                     NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_prompt_versions_project_id       ON prompt_versions(project_id);
CREATE INDEX idx_prompt_versions_imported_file_id ON prompt_versions(imported_file_id);

-- ─── Datasets ────────────────────────────────────────────────
CREATE TABLE datasets (
    id               UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id       UUID                     NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    imported_file_id UUID                     REFERENCES imported_files(id) ON DELETE SET NULL,
    name             VARCHAR(255)             NOT NULL,
    description      TEXT,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_datasets_project_id       ON datasets(project_id);
CREATE INDEX idx_datasets_imported_file_id ON datasets(imported_file_id);

-- ─── Test Cases ──────────────────────────────────────────────
-- Individual input/output pairs belonging to a dataset.
-- metadata is JSONB for flexible tagging (e.g. difficulty, category).
CREATE TABLE test_cases (
    id              UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    dataset_id      UUID                     NOT NULL REFERENCES datasets(id) ON DELETE CASCADE,
    input           TEXT                     NOT NULL,
    expected_output TEXT                     NOT NULL,
    metadata        JSONB,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_test_cases_dataset_id ON test_cases(dataset_id);

-- ─── Model Configurations ────────────────────────────────────
CREATE TABLE model_configurations (
    id          UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id  UUID                     NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    provider    VARCHAR(100)             NOT NULL,
    model_name  VARCHAR(255)             NOT NULL,
    temperature NUMERIC(4, 3),
    top_p       NUMERIC(4, 3),
    max_tokens  INTEGER,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_model_configurations_project_id ON model_configurations(project_id);

-- ─── Evaluation Configurations ───────────────────────────────
-- JSONB configuration gives full flexibility for any evaluation strategy.
CREATE TABLE evaluation_configurations (
    id            UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id    UUID                     NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name          VARCHAR(255)             NOT NULL,
    configuration JSONB                    NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_evaluation_configurations_project_id ON evaluation_configurations(project_id);

-- ─── Experiments ─────────────────────────────────────────────
-- ON DELETE RESTRICT on config FKs prevents orphaned experiments
-- if referenced resources are (accidentally) deleted.
CREATE TABLE experiments (
    id                          UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id                  UUID                     NOT NULL REFERENCES projects(id)                  ON DELETE CASCADE,
    prompt_version_id           UUID                     NOT NULL REFERENCES prompt_versions(id)           ON DELETE RESTRICT,
    dataset_id                  UUID                     NOT NULL REFERENCES datasets(id)                  ON DELETE RESTRICT,
    model_configuration_id      UUID                     NOT NULL REFERENCES model_configurations(id)      ON DELETE RESTRICT,
    evaluation_configuration_id UUID                     NOT NULL REFERENCES evaluation_configurations(id) ON DELETE RESTRICT,
    status                      VARCHAR(50)              NOT NULL DEFAULT 'PENDING',
    started_at                  TIMESTAMP WITH TIME ZONE,
    completed_at                TIMESTAMP WITH TIME ZONE,
    created_at                  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_experiments_project_id         ON experiments(project_id);
CREATE INDEX idx_experiments_status             ON experiments(status);
CREATE INDEX idx_experiments_prompt_version_id  ON experiments(prompt_version_id);
CREATE INDEX idx_experiments_dataset_id         ON experiments(dataset_id);

-- ─── Evaluation Results ──────────────────────────────────────
-- NUMERIC(5,4) = max 9.9999; accommodates 0–1 normalised scores.
-- raw_metrics JSONB preserves the full evaluation payload.
CREATE TABLE evaluation_results (
    id                  UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    experiment_id       UUID                     NOT NULL REFERENCES experiments(id) ON DELETE CASCADE,
    overall_score       NUMERIC(5, 4),
    quality_score       NUMERIC(5, 4),
    latency_score       NUMERIC(5, 4),
    hallucination_score NUMERIC(5, 4),
    cost_score          NUMERIC(5, 4),
    diagnosis           TEXT,
    recommendation      TEXT,
    raw_metrics         JSONB,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_evaluation_results_experiment_id ON evaluation_results(experiment_id);
