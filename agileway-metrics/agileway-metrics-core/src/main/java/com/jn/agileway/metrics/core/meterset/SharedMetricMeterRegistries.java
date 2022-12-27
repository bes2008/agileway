package com.jn.agileway.metrics.core.meterset;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A map of shared, named metric registries.
 *
 * @since 4.1.0
 */
public class SharedMetricMeterRegistries {
    private static final ConcurrentMap<String, MetricMeterRegistry> REGISTRIES = new ConcurrentHashMap<String, MetricMeterRegistry>();

    private SharedMetricMeterRegistries() { /* singleton */ }

    public static void clear() {
        REGISTRIES.clear();
    }

    public static Set<String> names() {
        return REGISTRIES.keySet();
    }

    public static void remove(String key) {
        REGISTRIES.remove(key);
    }

    public static MetricMeterRegistry add(String name, MetricMeterRegistry registry) {
        return REGISTRIES.putIfAbsent(name, registry);
    }

    public static MetricMeterRegistry getOrCreate(String name) {
        final MetricMeterRegistry existing = REGISTRIES.get(name);
        if (existing == null) {
            final MetricMeterRegistry created = new DefaultMetricRegistry();
            final MetricMeterRegistry raced = add(name, created);
            if (raced == null) {
                return created;
            }
            return raced;
        }
        return existing;
    }
}
