package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.metricset.MetricBuilder;
import com.jn.agileway.metrics.core.MetricName;
import com.jn.agileway.metrics.core.meter.Meter;
import com.jn.langx.util.reflect.Reflects;

/**
 * @since 4.1.0
 */
public class MeterBuilder extends AbstractMetricBuilder<Meter> {
    @Override
    public Meter newMetric(MetricName name) {
        return new MeterImpl(this.interval);
    }

    @Override
    public boolean isInstance(Metric metric) {
        return Reflects.isInstance(Meter.class, metric.getClass());
    }

    @Override
    public MetricBuilder<Meter> newBuilder() {
        return new MeterBuilder();
    }

}
