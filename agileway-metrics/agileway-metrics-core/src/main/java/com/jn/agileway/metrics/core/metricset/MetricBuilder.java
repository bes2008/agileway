package com.jn.agileway.metrics.core.metricset;


import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.MetricName;

public interface MetricBuilder<T extends Metric> {

    /**
     * create a new metric instance
     *
     * @param name the name of the metric
     * @return a metric instance
     */
    T newMetric(MetricName name);

    /**
     * check if the current builder can build the given metric
     *
     * @param metric the metric to check
     * @return true if the current builder can build this metric
     */
    boolean isInstance(Metric metric);

    MetricBuilder<T> interval(int interval);

    MetricBuilder<T> newBuilder();
}
