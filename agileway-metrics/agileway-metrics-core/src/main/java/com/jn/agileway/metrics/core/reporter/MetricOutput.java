package com.jn.agileway.metrics.core.reporter;

import com.jn.agileway.metrics.core.meterset.MetricMeterRegistry;
import com.jn.agileway.metrics.core.predicate.MetricMeterPredicate;

public interface MetricOutput {
    void write(MetricMeterRegistry registry, MetricMeterPredicate predicate);
}
