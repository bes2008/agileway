package com.jn.agileway.metrics.core.metricset;

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
