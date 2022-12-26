package com.jn.agileway.metrics.core.collector;

import com.jn.agileway.metrics.core.MetricName;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meter.impl.ClusterHistogram;

/**
 * The GOF Visitor pattern.
 * https://dzone.com/articles/design-patterns-visitor
 *
 * @since 4.1.0
 */
public interface Collector {

    void collect(MetricName name, Counter counter, long timestamp);

    void collect(MetricName name, Gauge gauge, long timestamp);

    void collect(MetricName name, Metered meter, long timestamp);

    void collect(MetricName name, Histogram histogram, long timestamp);

    void collect(MetricName name, Timer timer, long timestamp);

    void collect(MetricName name, Compass compass, long timestamp);

    void collect(MetricName name, FastCompass fastCompass, long timestamp);

    void collect(MetricName name, ClusterHistogram clusterHistogram, long timestamp);
}
