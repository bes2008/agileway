package com.jn.agileway.metrics.core;

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
