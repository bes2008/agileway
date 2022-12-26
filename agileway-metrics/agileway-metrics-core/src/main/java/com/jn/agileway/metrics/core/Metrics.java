package com.jn.agileway.metrics.core;


public class Metrics {
    private Metrics() {
    }

    /**
     * A special number to represent an invalid data
     * 一个特殊的数字来代表一个无效的结果
     */
    public static long NOT_AVAILABLE = -10001L;


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
