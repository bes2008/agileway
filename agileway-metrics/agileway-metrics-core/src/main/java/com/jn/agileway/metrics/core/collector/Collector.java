package com.jn.agileway.metrics.core.collector;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meter.impl.ClusterHistogram;

/**
 * The GOF Visitor pattern.
 * https://dzone.com/articles/design-patterns-visitor
 *
 * @since 4.1.0
 */
public interface Collector {

    void collect(Metric name, Counter counter, long timestamp);

    void collect(Metric name, Gauge gauge, long timestamp);

    void collect(Metric name, Metered meter, long timestamp);

    void collect(Metric name, Histogram histogram, long timestamp);

    void collect(Metric name, Timer timer, long timestamp);

    void collect(Metric name, Compass compass, long timestamp);

    void collect(Metric name, FastCompass fastCompass, long timestamp);

    void collect(Metric name, ClusterHistogram clusterHistogram, long timestamp);
}
