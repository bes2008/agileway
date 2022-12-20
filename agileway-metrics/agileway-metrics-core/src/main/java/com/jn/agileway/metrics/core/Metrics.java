package com.jn.agileway.metrics.core;

public class Metrics {
    private static final MetricRegistry INSTANCE = new MetricRegistryImpl();

    private Metrics() {
    }

    public static MetricRegistry global() {
        return INSTANCE;
    }
}
