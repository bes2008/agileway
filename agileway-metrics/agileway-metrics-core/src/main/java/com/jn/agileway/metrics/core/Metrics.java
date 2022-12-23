package com.jn.agileway.metrics.core;

import com.jn.agileway.metrics.core.metricset.MetricRegistry;
import com.jn.agileway.metrics.core.metricset.DefaultMetricRegistry;

public class Metrics {
    private static final MetricRegistry INSTANCE = new DefaultMetricRegistry();

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

    /**
     * Shorthand method for backwards compatibility in creating metric names.
     * <p>
     * Uses {@link MetricName#build(String...)} for its
     * heavy lifting.
     *
     * @param name  The first element of the name
     * @param names The remaining elements of the name
     * @return A metric name matching the specified components.
     * @see MetricName#build(String...)
     */
    public static MetricName name(String name, String... names) {
        final int length;

        if (names == null) {
            length = 0;
        } else {
            length = names.length;
        }

        final String[] parts = new String[length + 1];
        parts[0] = name;

        for (int i = 0; i < length; i++) {
            parts[i + 1] = names[i];
        }

        return MetricName.build(parts);
    }
    // ******************** start static method ************************

    /**
     * @see #name(String, String...)
     */
    public static MetricName name(Class<?> klass, String... names) {
        return name(klass.getName(), names);
    }

}
