package com.jn.agileway.metrics.core.meter;

import com.jn.agileway.metrics.core.snapshot.Snapshot;

/**
 * An object which samples values.
 * @since 4.1.0
 */
interface Sampling {
    /**
     * Returns a snapshot of the values.
     *
     * @return a snapshot of the values
     */
    Snapshot getSnapshot();
}
