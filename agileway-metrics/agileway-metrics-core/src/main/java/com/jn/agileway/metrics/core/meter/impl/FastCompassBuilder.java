package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.MetricBuilder;
import com.jn.agileway.metrics.core.MetricName;
import com.jn.agileway.metrics.core.meter.FastCompass;
import com.jn.langx.util.reflect.Reflects;

public class FastCompassBuilder extends AbstractMetricBuilder<FastCompass> {
    @Override
    public FastCompass newMetric(MetricName name) {
        return new FastCompassImpl(this.interval);
    }

    @Override
    public boolean isInstance(Metric metric) {
        return Reflects.isInstance(FastCompass.class, metric.getClass());
    }


    @Override
    public MetricBuilder<FastCompass> newBuilder() {
        return new FastCompassBuilder();
    }
}
