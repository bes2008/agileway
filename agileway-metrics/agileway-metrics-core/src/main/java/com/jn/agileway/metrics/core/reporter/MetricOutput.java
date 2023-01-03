package com.jn.agileway.metrics.core.reporter;

import com.jn.agileway.metrics.core.meterset.MetricMeterRegistry;

public interface MetricOutput {
    void write(MetricMeterRegistry registry);
}
