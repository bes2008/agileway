package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.meterset.MetricMeterBuilder;
import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.meter.Metered;
import com.jn.langx.util.reflect.Reflects;

/**
 * @since 4.1.0
 */
public class MeteredBuilder extends AbstractMetricBuilder<Metered> {
    @Override
    public Metered newMetric(Metric name) {
        return new MeteredImpl(this.interval);
    }

    @Override
    public boolean isInstance(Meter metric) {
        return Reflects.isInstance(metric, Metered.class);
    }

    @Override
    public MetricMeterBuilder<Metered> newBuilder() {
        return new MeteredBuilder();
    }

}
