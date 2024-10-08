package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.meter.Histogram;
import com.jn.agileway.metrics.core.snapshot.ReservoirType;
import com.jn.agileway.metrics.core.snapshot.ReservoirTypeMetricBuilder;
import com.jn.langx.util.reflect.Reflects;

/**
 * @since 4.1.0
 */
public class HistogramBuilder extends ReservoirTypeMetricBuilder<Histogram> {
    @Override
    public Histogram newMetric(Metric name) {
        return new HistogramImpl(this.interval);
    }

    @Override
    public Histogram newMetric(Metric name, ReservoirType type) {
        return new HistogramImpl(this.interval, type);
    }

    @Override
    public boolean isInstance(Meter metric) {
        return Reflects.isInstance(metric, Histogram.class);
    }

    @Override
    public HistogramBuilder newBuilder() {
        return new HistogramBuilder();
    }

}
