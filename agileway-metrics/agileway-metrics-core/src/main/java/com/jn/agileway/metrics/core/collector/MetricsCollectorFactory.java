package com.jn.agileway.metrics.core.collector;

import com.jn.agileway.metrics.core.predicate.MetricPredicate;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 4.1.0
 */
public class MetricsCollectorFactory {

    public static MetricsCollector createNew(Map<String, String> globalTags,
                                             double rateFactor, double durationFactor) {
        return createNew(CollectLevel.NORMAL, globalTags, rateFactor, durationFactor, null);
    }

    public static MetricsCollector createNew(CollectLevel level, Map<String, String> globalTags,
                                             double rateFactor, double durationFactor) {
        return createNew(level, globalTags, rateFactor, durationFactor, null);
    }

    public static MetricsCollector createNew(CollectLevel level, double rateFactor, double durationFactor,
                                             MetricPredicate filter) {
        return createNew(level, new HashMap<String, String>(), rateFactor, durationFactor, filter);
    }

    public static MetricsCollector createNew(double rateFactor, double durationFactor, MetricPredicate filter) {
        return createNew(CollectLevel.NORMAL, new HashMap<String, String>(), rateFactor, durationFactor, filter);
    }

    public static MetricsCollector createNew(CollectLevel collectLevel, Map<String, String> globalTags, double rateFactor,
                                             double durationFactor, MetricPredicate filter) {

        switch (collectLevel) {
            case COMPACT:
                return new CompactMetricsCollector(globalTags, rateFactor, durationFactor, filter);
            case NORMAL:
                return new NormalMetricsCollector(globalTags, rateFactor, durationFactor, filter);
            case CLASSIFIER:
                return new ClassifiedMetricsCollector(globalTags, rateFactor, durationFactor, filter);
            case COMPLETE:
                // FIXME: currently not supported
                throw new UnsupportedOperationException("Currently not supported!");
            default:
                throw new IllegalStateException("Unsupported CollectLevel: " + collectLevel);
        }

    }


}
