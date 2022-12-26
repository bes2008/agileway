package com.jn.agileway.metrics.core.meterset;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.meter.*;

import java.util.EventListener;

/**
 * Listeners for events from the registry.  Listeners must be thread-safe.
 *
 * @since 4.1.0
 */
public interface MetricRegistryListener extends EventListener {
    /**
     * Called when a {@link Gauge} is added to the registry.
     *
     * @param name  the gauge's name
     * @param gauge the gauge
     */
    void onGaugeAdded(Metric name, Gauge<?> gauge);

    /**
     * Called when a {@link Gauge} is removed from the registry.
     *
     * @param name the gauge's name
     */
    void onGaugeRemoved(Metric name);

    /**
     * Called when a {@link Counter} is added to the registry.
     *
     * @param name    the counter's name
     * @param counter the counter
     */
    void onCounterAdded(Metric name, Counter counter);

    /**
     * Called when a {@link Counter} is removed from the registry.
     *
     * @param name the counter's name
     */
    void onCounterRemoved(Metric name);

    /**
     * Called when a {@link Histogram} is added to the registry.
     *
     * @param name      the histogram's name
     * @param histogram the histogram
     */
    void onHistogramAdded(Metric name, Histogram histogram);

    /**
     * Called when a {@link Histogram} is removed from the registry.
     *
     * @param name the histogram's name
     */
    void onHistogramRemoved(Metric name);

    /**
     * Called when a {@link Metered} is added to the registry.
     *
     * @param name  the meter's name
     * @param meter the meter
     */
    void onMeterAdded(Metric name, Metered meter);

    /**
     * Called when a {@link Metered} is removed from the registry.
     *
     * @param name the meter's name
     */
    void onMeterRemoved(Metric name);

    /**
     * Called when a {@link Timer} is added to the registry.
     *
     * @param name  the timer's name
     * @param timer the timer
     */
    void onTimerAdded(Metric name, Timer timer);

    /**
     * Called when a {@link Timer} is removed from the registry.
     *
     * @param name the timer's name
     */
    void onTimerRemoved(Metric name);

    /**
     * Called when a {@link Compass} is added to the registry.
     *
     * @param name    the compass's name
     * @param compass the compass object added
     */
    void onCompassAdded(Metric name, Compass compass);

    /**
     * Called when a {@link Compass} is removed from the registry.
     *
     * @param name the compass's name
     */
    void onCompassRemoved(Metric name);

    /**
     * Called when a {@link FastCompass} is added to the registry.
     *
     * @param name    the FastCompass's name
     * @param compass the FastCompass object added
     */
    void onFastCompassAdded(Metric name, FastCompass compass);

    /**
     * Called when a {@link FastCompass} is removed from the registry.
     *
     * @param name the FastCompass's name
     */
    void onFastCompassRemoved(Metric name);

}
