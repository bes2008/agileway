package com.jn.agileway.metrics.core.noop;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meterset.MetricRegistryListener;

/**
 * @since 4.1.0
 */
abstract class NoopMetricRegistryListener implements MetricRegistryListener {
    @Override
    public void onGaugeAdded(Metric name, Gauge<?> gauge) {
    }

    @Override
    public void onGaugeRemoved(Metric name) {
    }

    @Override
    public void onCounterAdded(Metric name, Counter counter) {
    }

    @Override
    public void onCounterRemoved(Metric name) {
    }

    @Override
    public void onHistogramAdded(Metric name, Histogram histogram) {
    }

    @Override
    public void onHistogramRemoved(Metric name) {
    }

    @Override
    public void onMeterAdded(Metric name, Metered meter) {
    }

    @Override
    public void onMeterRemoved(Metric name) {
    }

    @Override
    public void onTimerAdded(Metric name, Timer timer) {
    }

    @Override
    public void onTimerRemoved(Metric name) {
    }

    @Override
    public void onCompassAdded(Metric name, Compass compass) {

    }

    @Override
    public void onCompassRemoved(Metric name) {

    }

    @Override
    public void onFastCompassAdded(Metric name, FastCompass compass) {

    }

    @Override
    public void onFastCompassRemoved(Metric name) {

    }
}