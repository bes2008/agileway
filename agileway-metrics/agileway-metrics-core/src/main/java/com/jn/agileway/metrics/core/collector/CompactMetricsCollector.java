package com.jn.agileway.metrics.core.collector;

import com.jn.agileway.metrics.core.*;
import com.jn.agileway.metrics.core.predicate.MetricPredicate;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.snapshot.Snapshot;

import java.util.Map;

/**
 * The collector implementation that only collect minimal metrics
 *
 * @since 4.1.0
 */
public class CompactMetricsCollector extends MetricsCollector {

    CompactMetricsCollector(Map<String, String> globalTags, double rateFactor,
                            double durationFactor, MetricPredicate filter) {
        super(globalTags, rateFactor, durationFactor, filter);
    }

    @Override
    public void collect(MetricName name, Timer timer, long timestamp) {
        final Snapshot snapshot = timer.getSnapshot();
        this.addMetric(name, "count", timer.getCount(), timestamp, MetricObject.MetricType.COUNTER)
                // convert rate
                .addMetric(name, "m1", convertRate(timer.getM1Rate()), timestamp)
                // convert duration
                .addMetric(name, "rt", convertDuration(snapshot.getMean()), timestamp)
                .addMetric(name, "mean", convertDuration(snapshot.getMean()), timestamp);

        // instant count
        addInstantCountMetric(timer.getInstantCount(), name, timer.getInstantCountInterval(), timestamp);
    }

    @Override
    public void collect(MetricName name, Histogram histogram, long timestamp) {
        final Snapshot snapshot = histogram.getSnapshot();
        this.addMetric(name, "mean", snapshot.getMean(), timestamp);
    }

    @Override
    public void collect(MetricName name, Compass compass, long timestamp) {
        final Snapshot snapshot = compass.getSnapshot();
        this.addMetric(name, "count", compass.getCount(), timestamp, MetricObject.MetricType.COUNTER)
                // convert rate
                .addMetric(name, "m1", convertRate(compass.getM1Rate()), timestamp)
                // convert duration
                .addMetric(name, "rt", convertDuration(snapshot.getMean()), timestamp)
                .addMetric(name, "mean", convertDuration(snapshot.getMean()), timestamp);

        // instant count
        addInstantCountMetric(compass.getInstantCount(), name, compass.getInstantCountInterval(), timestamp);

        // instant success count
        addInstantSuccessCount(name, compass, timestamp);

        // instant error code count
        addCompassErrorCode(name, compass, timestamp);

        // instant addon count
        addAddonMetric(name, compass, timestamp);
    }

    @Override
    public void collect(MetricName name, Metered meter, long timestamp) {
        this.addMetric(name, "count", meter.getCount(), timestamp, MetricObject.MetricType.COUNTER)
                // convert rate
                .addMetric(name, "m1", convertRate(meter.getM1Rate()), timestamp);

        // instant count
        addInstantCountMetric(meter.getInstantCount(), name, meter.getInstantCountInterval(), timestamp);
    }

    @Override
    public void collect(MetricName name, Counter counter, long timestamp) {
        MetricName normalizedName = name.getKey().endsWith("count") ? name : name.resolve("count");
        this.addMetric(normalizedName, counter.getCount(), timestamp, MetricObject.MetricType.COUNTER,
                metricsCollectPeriodConfig.period(name.getMetricLevel()));

        if (counter instanceof BucketCounter) {
            int countInterval = ((BucketCounter) counter).getBucketInterval();
            // bucket count
            addInstantCountMetric(((BucketCounter) counter).getBucketCounts(), name, countInterval, timestamp);
        }
    }

    @Override
    public void collect(MetricName name, Gauge gauge, long timestamp) {
        this.addMetric(name, gauge.getValue(), timestamp, MetricObject.MetricType.GAUGE,
                metricsCollectPeriodConfig.period(name.getMetricLevel()));
    }
}
