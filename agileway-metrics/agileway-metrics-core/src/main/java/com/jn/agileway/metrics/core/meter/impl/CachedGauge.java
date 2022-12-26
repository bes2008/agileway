package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.meter.Gauge;
import com.jn.langx.util.timing.clock.Clock;
import com.jn.langx.util.timing.clock.Clocks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@link Gauge} implementation which caches its value for a period of time.
 *
 * @since 4.1.0
 */
public abstract class CachedGauge<T> implements Gauge<T> {
    private final Clock clock;
    private final AtomicLong reloadAt;
    private final long timeoutNS;
    private long lastUpdate = System.currentTimeMillis();

    private volatile T value;

    /**
     * Creates a new cached gauge with the given timeout period.
     *
     * @param timeout     the timeout
     * @param timeoutUnit the unit of {@code timeout}
     */
    protected CachedGauge(long timeout, TimeUnit timeoutUnit) {
        this(null, timeout, timeoutUnit);
    }

    /**
     * Creates a new cached gauge with the given clock and timeout period.
     *
     * @param clock       the clock used to calculate the timeout
     * @param timeout     the timeout
     * @param timeoutUnit the unit of {@code timeout}
     */
    protected CachedGauge(Clock clock, long timeout, TimeUnit timeoutUnit) {
        clock = clock==null ? Clocks.defaultClock(): clock;
        this.clock = clock;
        this.reloadAt = new AtomicLong(clock.getTick());
        this.timeoutNS = timeoutUnit.toNanos(timeout);
    }

    /**
     * Loads the value and returns it.
     *
     * @return the new value
     */
    protected abstract T loadValue();

    public T getValue() {
        if (shouldLoad()) {
            this.value = loadValue();
            lastUpdate = System.currentTimeMillis();
        }
        return value;
    }

    @Override
    public long lastUpdateTime() {
        return lastUpdate;
    }

    private boolean shouldLoad() {
        for (; ; ) {
            final long currentTick = clock.getTick();
            final long reloadAtTick = reloadAt.get();
            if (currentTick < reloadAtTick) {
                return false;
            }
            if (reloadAt.compareAndSet(reloadAtTick, currentTick + timeoutNS)) {
                return true;
            }
        }
    }
}
