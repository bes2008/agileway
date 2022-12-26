package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.meter.Timer;
import com.jn.agileway.metrics.core.snapshot.ReservoirType;
import com.jn.agileway.metrics.core.snapshot.ReservoirTypeMetricBuilder;
import com.jn.langx.util.reflect.Reflects;

/**
 * @since 4.1.0
 */
public class TimerBuilder extends ReservoirTypeMetricBuilder<Timer> {


    @Override
    public boolean isInstance(Meter metric) {
        return Reflects.isInstance(metric, Timer.class);
    }

    @Override
    public TimerBuilder newBuilder() {
        return new TimerBuilder();
    }

    @Override
    public Timer newMetric(Metric name) {
        return new TimerImpl(this.interval);
    }

    @Override
    public Timer newMetric(Metric name, ReservoirType type) {
        return new TimerImpl(this.interval, type);
    }
}
