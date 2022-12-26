package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.meter.Gauge;

/**
 * A subclass of {@link Gauge} which should be persistent.
 * A gauge that is never invalidated.
 *
 *
 * @since 4.1.0
 */
public abstract class PersistentGauge<T> implements Gauge<T> {

    /**
     * This gauge is always available, and be updated constantly.
     */
    @Override
    public long lastUpdateTime() {
        return System.currentTimeMillis();
    }
}
