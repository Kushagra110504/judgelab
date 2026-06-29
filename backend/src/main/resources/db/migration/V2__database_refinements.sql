-- ============================================================
-- V2__database_refinements.sql
-- JudgeLab schema refinements (Ticket-002A)
-- ============================================================

-- ─── 1. One-to-One: evaluation_results → experiments ─────────
-- Each experiment produces exactly one evaluation result.
-- The UNIQUE constraint enforces this at the database level.
ALTER TABLE evaluation_results
    ADD CONSTRAINT uq_evaluation_results_experiment_id UNIQUE (experiment_id);

-- ─── 2. Prompt version display name ──────────────────────────
-- version_name is a short human-readable label for UI display
-- (e.g. "baseline", "few-shot", "cot-v2"). Nullable so existing
-- rows are unaffected.
ALTER TABLE prompt_versions
    ADD COLUMN version_name VARCHAR(100);

-- ─── 3. Imported file metadata ───────────────────────────────
-- file_size (bytes) and last_modified enable change detection
-- during folder synchronisation, preventing redundant re-imports.
-- Both nullable so existing rows are unaffected.
ALTER TABLE imported_files
    ADD COLUMN file_size      BIGINT,
    ADD COLUMN last_modified  TIMESTAMP WITH TIME ZONE;
