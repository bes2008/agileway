package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.metricset.MetricBuilder;
import com.jn.agileway.metrics.core.MetricName;
import com.jn.langx.util.reflect.Reflects;

/**
 * @since 4.1.0
 */
public class ClusterHistogramBuilder extends AbstractMetricBuilder<ClusterHistogram> {

    /**
     * Create a <T extends Metrics> instance with given name and buckets
     *
     * @param name    the name of the metric
     * @param buckets an array of long values
     * @return a metric implementation
     */
    public ClusterHistogram newMetric(MetricName name, long[] buckets){
        return new ClusterHistogramImpl(buckets, this.interval, null);
    }

    @Override
    public ClusterHistogram newMetric(MetricName name) {
        return new ClusterHistogramImpl(this.interval, null);
    }

    @Override
    public boolean isInstance(Metric metric) {
        return Reflects.isInstance(metric, ClusterHistogram.class);
    }

    @Override
    public MetricBuilder<ClusterHistogram> newBuilder() {
        return new ClusterHistogramBuilder();
    }
}
