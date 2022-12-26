package com.jn.agileway.metrics.core.metricset;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.MetricName;

import java.util.Map;

/**
 * A set of named metrics.
 *
 * @see MetricRegistry#registerAll(MetricSet)
 *
 * @since 4.1.0
 */
public interface MetricSet{
    /**
     * A map of metric names to metrics.
     *
     * @return the metrics
     */
    Map<MetricName, Metric> getMetrics();
}
