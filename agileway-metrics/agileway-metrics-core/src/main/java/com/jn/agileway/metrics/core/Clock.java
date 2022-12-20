package com.jn.agileway.metrics.core;

/**
 * Used to measure absolute and relative time.
 *
 * @see MockClock for a clock that can be manually advanced for use in tests.
 * @author Jon Schneider
 */
public interface Clock {

    Clock SYSTEM = new Clock() {
        @Override
        public long wallTime() {
            return System.currentTimeMillis();
        }

        @Override
        public long monotonicTime() {
            return System.nanoTime();
        }
    };

    /**
     * Current wall time in milliseconds since the epoch. Typically equivalent to
     * System.currentTimeMillis. Should not be used to determine durations. Used for
     * timestamping metrics being pushed to a monitoring system or for determination of
     * step boundaries (e.g. {@link
     * @return Wall time in milliseconds
     */
    long wallTime();

    /**
     * Current time from a monotonic clock source. The value is only meaningful when
     * compared with another snapshot to determine the elapsed time for an operation. The
     * difference between two samples will have a unit of nanoseconds. The returned value
     * is typically equivalent to System.nanoTime.
     * @return Monotonic time in nanoseconds
     */
    long monotonicTime();

}
