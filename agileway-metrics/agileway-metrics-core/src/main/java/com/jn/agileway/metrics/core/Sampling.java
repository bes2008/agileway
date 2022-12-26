package com.jn.agileway.metrics.core;

import com.jn.agileway.metrics.core.snapshot.Snapshot;

/**
 * An object which samples values.
 */
public interface Sampling {
    /**
     * Returns a snapshot of the values.
     *
     * @return a snapshot of the values
     */
    Snapshot getSnapshot();
}
