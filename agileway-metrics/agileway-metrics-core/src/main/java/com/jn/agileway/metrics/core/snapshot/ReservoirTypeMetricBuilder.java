package com.jn.agileway.metrics.core.snapshot;

import com.jn.agileway.metrics.core.AbstractMetricBuilder;
import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.MetricName;

public abstract class ReservoirTypeMetricBuilder<T extends Metric> extends AbstractMetricBuilder<T> implements ReservoirTypeBuilder<T> {
    protected int interval;
    @Override
    public abstract T newMetric(MetricName name);

    @Override
    public abstract boolean isInstance(Metric metric);

    @Override
    public abstract ReservoirTypeMetricBuilder<T> newBuilder();

    public ReservoirTypeMetricBuilder<T> interval(int interval) {
        this.interval = interval;
        return this;
    }

    @Override
    public abstract T newMetric(MetricName name, ReservoirType type);
}
