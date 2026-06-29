package com.judgelab.experiment;

/**
 * Lifecycle states for an {@link Experiment}.
 */
public enum ExperimentStatus {

    /** Experiment has been created but not yet submitted for execution. */
    PENDING,

    /** Experiment is actively being executed. */
    RUNNING,

    /** Experiment finished successfully. */
    COMPLETED,

    /** Experiment encountered an error and was aborted. */
    FAILED
}
