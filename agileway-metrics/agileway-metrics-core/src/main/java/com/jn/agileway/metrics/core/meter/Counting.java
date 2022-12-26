package com.jn.agileway.metrics.core.meter;

/**
 * An interface for metric types which have counts.
 *
 * @since 4.1.0
 */
interface Counting {
    /**
     * Returns the current count.
     *
     * @return the current count
     */
    long getCount();
}
