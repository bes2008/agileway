package com.jn.agileway.metrics.core.predicate;


import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;
import com.jn.langx.util.function.Predicate2;

/**
 * A filter used to determine whether a metric should be reported, among other things or not .
 *
 * @since 4.1.0
 */
public interface MetricMeterPredicate extends Predicate2<Metric, Meter> {


    /**
     * Returns {@code true} if the metric matches the filter; {@code false} otherwise.
     *
     * @param name   the metric name
     * @param metric the metric
     * @return {@code true} if the metric matches the filter
     */
    boolean test(Metric name, Meter metric);
}
