package com.jn.agileway.metrics.core.snapshot;

import com.jn.agileway.metrics.core.meter.impl.AbstractMetricBuilder;
import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;

/**
 * @since 4.1.0
 */
public abstract class ReservoirTypeMetricBuilder<T extends Meter> extends AbstractMetricBuilder<T> implements ReservoirTypeBuilder<T> {
    protected int interval;
    @Override
    public abstract T newMetric(Metric name);

    @Override
    public abstract boolean isInstance(Meter metric);

    @Override
    public abstract ReservoirTypeMetricBuilder<T> newBuilder();

    public ReservoirTypeMetricBuilder<T> interval(int interval) {
        this.interval = interval;
        return this;
    }

    @Override
    public abstract T newMetric(Metric name, ReservoirType type);
}
