package com.jn.agileway.metrics.core;

/**
 * An interface to indicate that a class is a metric.
 *
 * @since 4.1.0
 */
public interface Meter {

    /**
     * Return the last update time in milliseconds
     *
     * @return the last updated time in milliseconds
     */
    long lastUpdateTime();
}
