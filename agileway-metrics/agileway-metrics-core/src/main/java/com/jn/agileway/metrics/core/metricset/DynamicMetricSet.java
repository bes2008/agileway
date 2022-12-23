package com.jn.agileway.metrics.core.metricset;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.MetricName;

import java.util.Map;

/**
 * A dynamic metric set.
 * The metrics inside will change dynamically.
 */
public interface DynamicMetricSet extends Metric {

    /**
     * A map of metric names to metrics.
     * The metrics inside will change dynamically.
     * So DO NOT register them at first time.
     *
     * @return the dynamically changing metrics
     */
    Map<MetricName, Metric> getDynamicMetrics();
}
