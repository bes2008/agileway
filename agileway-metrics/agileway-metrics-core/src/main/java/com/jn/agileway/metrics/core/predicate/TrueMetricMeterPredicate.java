package com.jn.agileway.metrics.core.predicate;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;

public class TrueMetricMeterPredicate implements MetricMeterPredicate {
    @Override
    public boolean test(Metric name, Meter metric) {
        return true;
    }
}
