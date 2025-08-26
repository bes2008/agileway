package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.meterset.MetricMeterBuilder;

/**
 * @since 4.1.0
 */
public abstract class AbstractMetricBuilder<T extends Meter> implements MetricMeterBuilder<T> {
    protected int interval;

    public AbstractMetricBuilder<T> interval(int interval) {
        this.interval = interval;
        return this;
    }

    @Override
    public abstract T newMetric(Metric name);

    @Override
    public abstract boolean isInstance(Meter metric);


    @Override
    public abstract MetricMeterBuilder<T> newBuilder();
}
