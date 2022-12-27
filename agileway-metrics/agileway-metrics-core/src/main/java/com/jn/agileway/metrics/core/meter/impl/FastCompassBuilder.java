package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.meterset.MetricMeterBuilder;
import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.meter.FastCompass;
import com.jn.langx.util.reflect.Reflects;

/**
 * @since 4.1.0
 */
public class FastCompassBuilder extends AbstractMetricBuilder<FastCompass> {
    @Override
    public FastCompass newMetric(Metric name) {
        return new FastCompassImpl(this.interval);
    }

    @Override
    public boolean isInstance(Meter metric) {
        return Reflects.isInstance(metric, FastCompass.class);
    }


    @Override
    public MetricMeterBuilder<FastCompass> newBuilder() {
        return new FastCompassBuilder();
    }
}
