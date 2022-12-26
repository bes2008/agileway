package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.metricset.MetricBuilder;
import com.jn.agileway.metrics.core.MetricName;
import com.jn.agileway.metrics.core.meter.FastCompass;
import com.jn.langx.util.reflect.Reflects;

/**
 * @since 4.1.0
 */
public class FastCompassBuilder extends AbstractMetricBuilder<FastCompass> {
    @Override
    public FastCompass newMetric(MetricName name) {
        return new FastCompassImpl(this.interval);
    }

    @Override
    public boolean isInstance(Metric metric) {
        return Reflects.isInstance(metric, FastCompass.class);
    }


    @Override
    public MetricBuilder<FastCompass> newBuilder() {
        return new FastCompassBuilder();
    }
}
