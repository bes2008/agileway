package com.jn.agileway.metrics.core.predicate;


import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.MetricName;
import com.jn.langx.util.function.Predicate2;

/**
 * A filter used to determine whether a metric should be reported, among other things or not .
 */
public interface MetricPredicate extends Predicate2<MetricName, Metric> {


    /**
     * Returns {@code true} if the metric matches the filter; {@code false} otherwise.
     *
     * @param name   the metric name
     * @param metric the metric
     * @return {@code true} if the metric matches the filter
     */
    boolean test(MetricName name, Metric metric);
}
