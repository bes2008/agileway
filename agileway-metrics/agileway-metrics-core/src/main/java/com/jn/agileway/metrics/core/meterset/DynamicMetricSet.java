package com.jn.agileway.metrics.core.meterset;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;

import java.util.Map;

/**
 * A dynamic metric set.
 * The metrics inside will change dynamically.
 *
 * @since 4.1.0
 */
public interface DynamicMetricSet extends Meter {

    /**
     * A map of metric names to metrics.
     * The metrics inside will change dynamically.
     * So DO NOT register them at first time.
     *
     * @return the dynamically changing metrics
     */
    Map<Metric, Meter> getDynamicMetrics();
}
