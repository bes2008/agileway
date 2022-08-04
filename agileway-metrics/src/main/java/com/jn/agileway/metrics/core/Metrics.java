package com.jn.agileway.metrics.core;

import com.codahale.metrics.MetricRegistry;

public class Metrics {
    private static final MetricRegistry INSTANCE = new MetricRegistry();

    private Metrics() {
    }

    public static MetricRegistry global() {
        return INSTANCE;
    }
}
