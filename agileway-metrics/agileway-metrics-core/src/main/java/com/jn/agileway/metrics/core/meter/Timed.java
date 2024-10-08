package com.jn.agileway.metrics.core.meter;

import com.jn.agileway.metrics.core.Meter;

import java.util.Map;

/**
 *
 * A meter measures the rate of events over time
 *
 * An object which maintains mean and exponentially-weighted rate.
 *
 *
 * @since 4.1.0
 */
interface Timed extends Meter, Counting {

    /**
     * Returns the number of events which have been marked.
     *
     * @return the number of events which have been marked
     */
    long getCount();

    /**
     * Get the accurate number per collecting interval
     *
     * @return an long array, each contains the number of events, keyed by timestamp in milliseconds
     */
    Map<Long, Long> getInstantCount();

    /**
     * Get the accurate number per collecting interval since (including) the start time
     *
     * @param startTime the start time of the query
     * @return an long array, each contains the number of events, keyed by timestamp in milliseconds
     */
    Map<Long, Long> getInstantCount(long startTime);

    /**
     * Get the collecting interval
     *
     * @return the collecting interval
     */
    int getInstantCountInterval();

    /**
     * Returns the fifteen-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created.
     * <p/>
     * This rate has the same exponential decay factor as the fifteen-minute load average in the
     * {@code top} Unix command.
     *
     * @return the fifteen-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created
     */
    double getM15Rate();

    /**
     * Returns the five-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created.
     * <p/>
     * This rate has the same exponential decay factor as the five-minute load average in the {@code
     * top} Unix command.
     *
     * @return the five-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created
     */
    double getM5Rate();

    /**
     * Returns the mean rate at which events have occurred since the meter was created.
     *
     * @return the mean rate at which events have occurred since the meter was created
     */
    double getMeanRate();

    /**
     * Returns the one-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created.
     * <p/>
     * This rate has the same exponential decay factor as the one-minute load average in the {@code
     * top} Unix command.
     *
     * @return the one-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created
     */
    double getM1Rate();
}
