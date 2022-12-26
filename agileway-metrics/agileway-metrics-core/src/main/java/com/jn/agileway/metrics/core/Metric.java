package com.jn.agileway.metrics.core;

/**
 * An interface to indicate that a class is a metric.
 */
public interface Metric {

    /**
     * Return the last update time in milliseconds
     *
     * @return the last updated time in milliseconds
     */
    long lastUpdateTime();
}
