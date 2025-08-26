package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.meter.Counter;
import com.jn.agileway.metrics.core.meterset.MetricMeterBuilder;
import com.jn.langx.util.reflect.Reflects;

/**
 * @since 4.1.0
 */
public class CounterBuilder extends AbstractMetricBuilder<Counter> {
    @Override
    public Counter newMetric(Metric name) {
        return  new BucketCounterImpl(this.interval);
    }

    @Override
    public boolean isInstance(Meter metric) {
        return Reflects.isInstance(metric, Counter.class);
    }

    @Override
    public MetricMeterBuilder<Counter> newBuilder() {
        return new CounterBuilder();
    }
}
