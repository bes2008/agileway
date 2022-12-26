package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.metricset.MetricBuilder;
import com.jn.agileway.metrics.core.MetricName;

/**
 * @since 4.1.0
 */
public abstract class AbstractMetricBuilder<T extends Meter> implements MetricBuilder<T> {
    protected int interval;

    public AbstractMetricBuilder<T> interval(int interval) {
        this.interval = interval;
        return this;
    }

    @Override
    public abstract T newMetric(MetricName name);

    @Override
    public abstract boolean isInstance(Meter metric);


    @Override
    public abstract MetricBuilder<T> newBuilder();
}
