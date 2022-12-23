package com.jn.agileway.metrics.core;

/**
 * enum的数值不能为负数，且不能太大
 */
public enum MetricLevel {

    TRIVIAL, // 轻微指标

    MINOR,   // 次要指标

    NORMAL,  // 一般指标

    MAJOR,   // 重要指标

    CRITICAL; // 关键指标

    static {
        for (MetricLevel level : MetricLevel.values()) {
            if (level.ordinal() < 0) {
                throw new RuntimeException("MetricLevel can not < 0");
            }
        }
    }

    public static int getMaxValue() {
        MetricLevel[] levels = MetricLevel.values();
        int max = levels[0].ordinal();
        for (MetricLevel level : levels) {
            int value = level.ordinal();
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
