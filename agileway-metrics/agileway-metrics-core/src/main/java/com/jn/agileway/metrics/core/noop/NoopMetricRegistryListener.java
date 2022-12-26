package com.jn.agileway.metrics.core.noop;

import com.jn.agileway.metrics.core.MetricName;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.metricset.MetricRegistryListener;

/**
 * @since 4.1.0
 */
abstract class NoopMetricRegistryListener implements MetricRegistryListener {
    @Override
    public void onGaugeAdded(MetricName name, Gauge<?> gauge) {
    }

    @Override
    public void onGaugeRemoved(MetricName name) {
    }

    @Override
    public void onCounterAdded(MetricName name, Counter counter) {
    }

    @Override
    public void onCounterRemoved(MetricName name) {
    }

    @Override
    public void onHistogramAdded(MetricName name, Histogram histogram) {
    }

    @Override
    public void onHistogramRemoved(MetricName name) {
    }

    @Override
    public void onMeterAdded(MetricName name, Meter meter) {
    }

    @Override
    public void onMeterRemoved(MetricName name) {
    }

    @Override
    public void onTimerAdded(MetricName name, Timer timer) {
    }

    @Override
    public void onTimerRemoved(MetricName name) {
    }

    @Override
    public void onCompassAdded(MetricName name, Compass compass) {

    }

    @Override
    public void onCompassRemoved(MetricName name) {

    }

    @Override
    public void onFastCompassAdded(MetricName name, FastCompass compass) {

    }

    @Override
    public void onFastCompassRemoved(MetricName name) {

    }
}