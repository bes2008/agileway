package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.MetricName;
import com.jn.agileway.metrics.core.meter.Compass;
import com.jn.agileway.metrics.core.snapshot.ReservoirType;
import com.jn.agileway.metrics.core.snapshot.ReservoirTypeMetricBuilder;
import com.jn.langx.util.reflect.Reflects;

/**
 * @since 4.1.0
 */
public class CompassBuilder extends ReservoirTypeMetricBuilder<Compass> {
    @Override
    public Compass newMetric(MetricName name) {
        return new CompassImpl(this.interval);
    }

    @Override
    public Compass newMetric(MetricName name, ReservoirType type) {
        return new CompassImpl(this.interval, type);
    }

    @Override
    public boolean isInstance(Metric metric) {
        return Reflects.isInstance(metric, Compass.class);
    }

    @Override
    public CompassBuilder newBuilder() {
        return new CompassBuilder();
    }


}
