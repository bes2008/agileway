package com.jn.agileway.metrics.core.meterset;


import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;

/**
 * @since 4.1.0
 * @param <T>
 */
public interface MetricMeterBuilder<T extends Meter> {

    /**
     * create a new metric instance
     *
     * @param name the name of the metric
     * @return a metric instance
     */
    T newMetric(Metric name);

    /**
     * check if the current builder can build the given metric
     *
     * @param metric the metric to check
     * @return true if the current builder can build this metric
     */
    boolean isInstance(Meter metric);

    MetricMeterBuilder<T> interval(int interval);

    MetricMeterBuilder<T> newBuilder();
}
