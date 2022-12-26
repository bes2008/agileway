package com.jn.agileway.metrics.core.meterset;

/**
 * @since 4.1.0
 */
public class MetricFactoryBinder {

    private static final MetricFactoryBinder instance = new MetricFactoryBinder();

    private MetricFactory factory;

    private MetricFactoryBinder() {
        factory = new DefaultMetricFactory();
    }

    public static MetricFactoryBinder getSingleton() {
        return instance;
    }

    public MetricFactory getMetricFactory() {
        return factory;
    }
}
