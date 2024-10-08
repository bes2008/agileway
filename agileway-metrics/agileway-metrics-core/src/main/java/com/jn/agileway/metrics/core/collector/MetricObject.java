package com.jn.agileway.metrics.core.collector;

import static com.jn.agileway.metrics.core.Metric.MetricLevel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 4.1.0
 */
public class MetricObject {

    /*
     * {
     *   "metric": "sys.cpu.nice",
     *   "timestamp": 1346846400,
     *   "value": 18,
     *   "type": "COUNTER",
     *   "level": "CRITICAL",
     *   "tags":
     *          { "host": "web01", "dc": "lga" }
     * }
     */

    private String metric;
    private Long timestamp;
    private Object value;
    private MetricType metricType;
    private Map<String, String> tags = new HashMap<String, String>();
    private MetricLevel metricLevel;
    private transient String meterName;
    /**
     * 分桶统计时间间隔，目前针对Meter/Timer/Compass有效，-1表示此项无效
     */
    private int interval = -1;

    public MetricObject() {

    }

    public static Builder named(String name) {
        return new Builder(name);
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof MetricObject)) {
            return false;
        }

        final MetricObject rhs = (MetricObject) o;

        return equals(metric, rhs.metric) && equals(tags, rhs.tags) && equals(metricType, rhs.metricType)
                && equals(metricLevel, rhs.metricLevel);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{metric, tags, metricType, metricLevel});
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "->metric: " + metric + ",value: "
                + value + ",timestamp: " + timestamp + ",type: " + metricType
                + ",tags: " + tags + ",level: " + metricLevel;
    }

    public String getMetric() {
        return metric;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Object getValue() {
        return value;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public MetricLevel getMetricLevel() {
        return metricLevel;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public int getInterval() {
        return interval;
    }

    public String getMeterName() {
        return meterName;
    }

    private boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }


    public enum MetricType {
        /**
         * 用于累加型的数据
         */
        COUNTER,
        /**
         * 用于瞬态数据
         */
        GAUGE,
        /**
         * 用于争分整秒的计数
         */
        DELTA,
        /**
         * 用于集群分位数计算
         */
        PERCENTILE
    }

    public static class Builder {

        private final MetricObject metric;

        public Builder(String name) {
            this.metric = new MetricObject();
            metric.metric = name;
        }

        public MetricObject build() {
            return metric;
        }

        public Builder withValue(Object value) {
            metric.value = value;
            return this;
        }

        public Builder withTimestamp(Long timestamp) {
            metric.timestamp = timestamp;
            return this;
        }

        public Builder withTags(Map<String, String> tags) {
            if (tags != null) {
                for (Map.Entry<String, String> entry : tags.entrySet()) {
                    metric.tags.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        public Builder withType(MetricType type) {
            metric.metricType = type;
            return this;
        }

        public Builder withLevel(MetricLevel level) {
            metric.metricLevel = level;
            return this;
        }

        public Builder withInterval(int interval) {
            metric.interval = interval;
            return this;
        }

        public Builder withMeterName(String meterName) {
            metric.meterName = meterName;
            return this;
        }

    }
}
