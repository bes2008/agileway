package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.meter.Histogram;
import com.jn.agileway.metrics.core.snapshot.*;
import com.jn.langx.util.timing.clock.Clock;
import com.jn.langx.util.timing.clock.Clocks;

import java.util.concurrent.TimeUnit;

/**
 * A metric which calculates the distribution of a value.
 *
 * @see <a href="http://www.johndcook.com/standard_deviation.html">Accurately computing running
 * variance</a>
 *
 * @since 4.1.0
 */
public class HistogramImpl implements Histogram {

    private final Reservoir reservoir;
    private final BucketCounter count;

    /**
     * Creates a new {@link Histogram} with the given reservoir.
     *
     * @param type the reservoir type to create a histogram from
     */
    public HistogramImpl(ReservoirType type) {
        this(type, 60, 10, Clocks.defaultClock());
    }

    public HistogramImpl(int interval, ReservoirType type) {
        this(type, interval, 10, Clocks.defaultClock());
    }

    public HistogramImpl(int interval) {
        this(ReservoirType.EXPONENTIALLY_DECAYING, interval, 10, Clocks.defaultClock());
    }

    /**
     * Creates a new {@link Histogram} with the given reservoir.
     *
     * @param type           the reservoir type to create a histogram from
     * @param interval       the interval to create bucket counter
     * @param numberOfBucket the number of bucket to create bucket counter
     * @param clock          the clock the create bucket counter
     */
    public HistogramImpl(ReservoirType type, int interval, int numberOfBucket, Clock clock) {
        clock = clock==null ? Clocks.defaultClock(): clock;
        this.count = new BucketCounterImpl(interval, numberOfBucket, clock);
        switch (type) {
            case EXPONENTIALLY_DECAYING:
                this.reservoir = new ExponentiallyDecayingReservoir(clock);
                break;
            case SLIDING_TIME_WINDOW:
                this.reservoir = new SlidingTimeWindowReservoir(interval, TimeUnit.SECONDS);
                break;
            case SLIDING_WINDOW:
                this.reservoir = new SlidingWindowReservoir(1024);
                break;
            case UNIFORM:
                this.reservoir = new UniformReservoir(1024);
                break;
            case BUCKET:
                this.reservoir = new BucketReservoir(interval, numberOfBucket, clock, count);
                break;
            default:
                this.reservoir = new ExponentiallyDecayingReservoir(clock);
        }
    }

    public HistogramImpl(Reservoir reservoir, int interval, int numberOfBucket, Clock clock) {
        this.reservoir = reservoir;
        this.count = new BucketCounterImpl(interval, numberOfBucket, clock);
    }

    /**
     * Adds a recorded value.
     *
     * @param value the length of the value
     */
    public void update(int value) {
        update((long) value);
    }

    /**
     * Adds a recorded value.
     *
     * @param value the length of the value
     */
    public void update(long value) {
        count.update();
        reservoir.update(value);
    }

    /**
     * Returns the number of values recorded.
     *
     * @return the number of values recorded
     */
    public long getCount() {
        return count.getCount();
    }

    public Snapshot getSnapshot() {
        return reservoir.getSnapshot();
    }

    @Override
    public long lastUpdateTime() {
        return count.lastUpdateTime();
    }
}
