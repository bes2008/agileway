package com.jn.agileway.metrics.core.snapshot;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.metricset.MetricBuilder;
import com.jn.agileway.metrics.core.MetricName;

/**
 * @since 4.1.0
 */
public interface ReservoirTypeBuilder<T extends Metric> extends MetricBuilder<T> {

    /**
     * Create a <T extends Metrics> instance with given reservoir type
     *
     * @param name the name of the metric
     * @param type the type of reservoir type specified in {@link ReservoirType}
     * @return a metric implementation
     */
    T newMetric(MetricName name, ReservoirType type);

    @Override
    MetricBuilder<T> newBuilder();
}
