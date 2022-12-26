package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.metricset.MetricBuilder;
import com.jn.agileway.metrics.core.MetricName;
import com.jn.agileway.metrics.core.meter.Counter;
import com.jn.langx.util.reflect.Reflects;

/**
 * @since 4.1.0
 */
public class CounterBuilder extends AbstractMetricBuilder<Counter> {
    @Override
    public Counter newMetric(MetricName name) {
        return  new BucketCounterImpl(this.interval);
    }

    @Override
    public boolean isInstance(Metric metric) {
        return Reflects.isInstance(metric, Counter.class);
    }

    @Override
    public MetricBuilder<Counter> newBuilder() {
        return new CounterBuilder();
    }
}
