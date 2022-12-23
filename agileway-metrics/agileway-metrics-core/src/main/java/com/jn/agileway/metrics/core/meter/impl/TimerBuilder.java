package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.MetricName;
import com.jn.agileway.metrics.core.meter.Timer;
import com.jn.agileway.metrics.core.snapshot.ReservoirType;
import com.jn.agileway.metrics.core.snapshot.ReservoirTypeMetricBuilder;
import com.jn.langx.util.reflect.Reflects;

public class TimerBuilder extends ReservoirTypeMetricBuilder<Timer> {


    @Override
    public boolean isInstance(Metric metric) {
        return Reflects.isInstance(Timer.class, metric.getClass());
    }

    @Override
    public TimerBuilder newBuilder() {
        return new TimerBuilder();
    }

    @Override
    public Timer newMetric(MetricName name) {
        return new TimerImpl(this.interval);
    }

    @Override
    public Timer newMetric(MetricName name, ReservoirType type) {
        return new TimerImpl(this.interval, type);
    }
}
