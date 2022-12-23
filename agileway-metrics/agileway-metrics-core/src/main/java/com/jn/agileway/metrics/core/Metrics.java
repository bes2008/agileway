package com.jn.agileway.metrics.core;

import com.jn.agileway.metrics.core.metricset.MetricRegistry;
import com.jn.agileway.metrics.core.metricset.MetricRegistryImpl;

public class Metrics {
    private static final MetricRegistry INSTANCE = new MetricRegistryImpl();

    private Metrics() {
    }

    public static MetricRegistry global() {
        return INSTANCE;
    }


    /**
     * Matches all metrics, regardless of type or name.
     */
   public static final MetricFilter ALL = new MetricFilter() {
        @Override
        public boolean matches(MetricName name, Metric metric) {
            return true;
        }
    };
}
