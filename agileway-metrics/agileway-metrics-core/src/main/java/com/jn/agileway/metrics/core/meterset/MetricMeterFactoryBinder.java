package com.jn.agileway.metrics.core.meterset;

/**
 * @since 4.1.0
 */
public class MetricMeterFactoryBinder {

    private static final MetricMeterFactoryBinder instance = new MetricMeterFactoryBinder();

    private MetricMeterFactory factory;

    private MetricMeterFactoryBinder() {
        factory = new DefaultMetricFactory();
    }

    public static MetricMeterFactoryBinder getSingleton() {
        return instance;
    }

    public MetricMeterFactory getMetricFactory() {
        return factory;
    }
}
