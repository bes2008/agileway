package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.MetricBuilder;
import com.jn.agileway.metrics.core.MetricName;

public abstract class AbstractMetricBuilder<T extends Metric> implements MetricBuilder<T> {
    protected int interval;

    public AbstractMetricBuilder<T> interval(int interval) {
        this.interval = interval;
        return this;
    }

    @Override
    public abstract T newMetric(MetricName name);

    @Override
    public abstract boolean isInstance(Metric metric);


    @Override
    public abstract MetricBuilder<T> newBuilder();
}