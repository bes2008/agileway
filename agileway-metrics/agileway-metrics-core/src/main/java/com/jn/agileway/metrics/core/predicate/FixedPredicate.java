package com.jn.agileway.metrics.core.predicate;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;

/**
 * @since 4.1.0
 */
public class FixedPredicate implements MetricPredicate{
    private final boolean value;
    public FixedPredicate(boolean value){
        this.value = value;
    }
    public static final FixedPredicate TRUE = new FixedPredicate(true);
    public static final FixedPredicate FALSE = new FixedPredicate(false);
    @Override
    public boolean test(Metric name, Meter metric) {
        return this.value;
    }
}
