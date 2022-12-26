package com.jn.agileway.metrics.core.meter;

/**
 * A meter metric which measures mean throughput and one-, five-, and fifteen-minute
 * exponentially-weighted moving average throughput.
 * 一种用于度量一段时间内吞吐率的计量器。例如，一分钟内，五分钟内，十五分钟内的qps指标，
 * 这段时间内的吞吐率通过指数加权的方式计算移动平均得出。
 *
 * @since 4.1.0
 */
public interface Meter extends Timed {

    /**
     * Mark the occurrence of an event.
     * 标记一次事件
     */
    void mark();

    /**
     * Mark the occurrence of a given number of events.
     * 标记n次事件
     *
     * @param n the number of events
     */
    void mark(long n);
}
