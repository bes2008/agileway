package com.jn.agileway.metrics.supports.prometheus;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.meter.Timer;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meterset.MetricMeterRegistry;
import com.jn.agileway.metrics.core.predicate.FixedPredicate;
import com.jn.agileway.metrics.core.predicate.MetricMeterPredicate;
import com.jn.agileway.metrics.core.snapshot.Snapshot;
import com.jn.agileway.metrics.supports.prometheus.simple.DefaultSampleBuilder;
import com.jn.agileway.metrics.supports.prometheus.simple.SampleBuilder;
import com.jn.langx.util.collection.Collects;
import io.prometheus.client.Collector;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @since 4.1.0
 */
public class PrometheusMetricsCollectorAdapter extends Collector implements Collector.Describable {
    private static final Logger LOGGER = Logger.getLogger(PrometheusMetricsCollectorAdapter.class.getName());
    private MetricMeterRegistry registry;
    private MetricMeterPredicate predicate;
    private SampleBuilder sampleBuilder;

    /**
     * Creates a new adapter with a {@link DefaultSampleBuilder} and {@link FixedPredicate#TRUE}.
     *
     * @param registry a metric registry to export in prometheus.
     */
    public PrometheusMetricsCollectorAdapter(MetricMeterRegistry registry) {
        this(registry, FixedPredicate.TRUE);
    }

    /**
     * Creates a new DropwizardExports with a {@link DefaultSampleBuilder} and custom {@link MetricMeterPredicate}.
     *
     * @param registry  a metric registry to export in prometheus.
     * @param predicate a custom metric filter.
     */
    public PrometheusMetricsCollectorAdapter(MetricMeterRegistry registry, MetricMeterPredicate predicate) {
        this(registry, predicate, new DefaultSampleBuilder());
    }

    /**
     * @param registry      a metric registry to export in prometheus.
     * @param sampleBuilder sampleBuilder to use to create prometheus samples.
     */
    public PrometheusMetricsCollectorAdapter(MetricMeterRegistry registry, SampleBuilder sampleBuilder) {
        this(registry, FixedPredicate.TRUE, sampleBuilder);
    }

    /**
     * @param registry      a metric registry to export in prometheus.
     * @param predicate     a custom metric filter.
     * @param sampleBuilder sampleBuilder to use to create prometheus samples.
     */
    public PrometheusMetricsCollectorAdapter(MetricMeterRegistry registry, MetricMeterPredicate predicate, SampleBuilder sampleBuilder) {
        this.registry = registry;
        this.predicate = predicate;
        this.sampleBuilder = sampleBuilder;
    }

    private static String getHelpMessage(String metricName, Meter metric) {
        return String.format("Generated from Dropwizard metric import (metric=%s, type=%s)",
                metricName, metric.getClass().getName());
    }

    /**
     * Export counter as Prometheus <a href="https://prometheus.io/docs/concepts/metric_types/#gauge">Gauge</a>.
     */
    MetricFamilySamples fromCounter(Metric metricName, Counter counter) {
        String dropwizardName = metricName.getKey();
        MetricFamilySamples.Sample sample = sampleBuilder.createSample(metricName, "", new ArrayList<String>(), new ArrayList<String>(),counter.getCount());
        return new MetricFamilySamples(sample.name, Type.GAUGE, getHelpMessage(dropwizardName, counter), Arrays.asList(sample));
    }

    /**
     * Export gauge as a prometheus gauge.
     */
    MetricFamilySamples fromGauge(Metric metricName, Gauge gauge) {
        Object obj = gauge.getValue();
        double value;
        String dropwizardName = metricName.getKey();
        if (obj instanceof Number) {
            value = ((Number) obj).doubleValue();
        } else if (obj instanceof Boolean) {
            value = ((Boolean) obj) ? 1 : 0;
        } else {
            LOGGER.log(Level.FINE, String.format("Invalid type for Gauge %s: %s", sanitizeMetricName(dropwizardName), obj == null ? "null" : obj.getClass().getName()));
            return null;
        }
        MetricFamilySamples.Sample sample = sampleBuilder.createSample(metricName, "",
                new ArrayList<String>(), new ArrayList<String>(), value);
        return new MetricFamilySamples(sample.name, Type.GAUGE, getHelpMessage(dropwizardName, gauge), Arrays.asList(sample));
    }

    /**
     * Export a histogram snapshot as a prometheus SUMMARY.
     *
     * @param metricName metric name.
     * @param snapshot   the histogram snapshot.
     * @param count      the total sample count for this snapshot.
     * @param factor     a factor to apply to histogram values.
     */
    MetricFamilySamples fromSnapshotAndCount(Metric metricName, Snapshot snapshot, long count, double factor, String helpMessage) {
        List<MetricFamilySamples.Sample> samples = Collects.asList(
                sampleBuilder.createSample(metricName, "", Collects.asList("quantile"), Collects.asList("0.5"), snapshot.getMedian() * factor),
                sampleBuilder.createSample(metricName, "", Collects.asList("quantile"), Collects.asList("0.75"), snapshot.get75thPercentile() * factor),
                sampleBuilder.createSample(metricName, "", Collects.asList("quantile"), Collects.asList("0.95"), snapshot.get95thPercentile() * factor),
                sampleBuilder.createSample(metricName, "", Collects.asList("quantile"), Collects.asList("0.98"), snapshot.get98thPercentile() * factor),
                sampleBuilder.createSample(metricName, "", Collects.asList("quantile"), Collects.asList("0.99"), snapshot.get99thPercentile() * factor),
                sampleBuilder.createSample(metricName, "", Collects.asList("quantile"), Collects.asList("0.999"), snapshot.get999thPercentile() * factor),
                sampleBuilder.createSample(metricName, "_count", new ArrayList<String>(), new ArrayList<String>(), count)
        );
        return new MetricFamilySamples(samples.get(0).name, Type.SUMMARY, helpMessage, samples);
    }

    /**
     * Convert histogram snapshot.
     */
    MetricFamilySamples fromHistogram(Metric metricName, Histogram histogram) {
        String dropwizardName = metricName.getKey();
        return fromSnapshotAndCount(metricName, histogram.getSnapshot(), histogram.getCount(), 1.0,
                getHelpMessage(dropwizardName, histogram));
    }

    /**
     * Export Dropwizard Timer as a histogram. Use TIME_UNIT as time unit.
     */
    MetricFamilySamples fromTimer(Metric metricName, Timer timer) {
        String dropwizardName = metricName.getKey();
        return fromSnapshotAndCount(metricName, timer.getSnapshot(), timer.getCount(),
                1.0D / TimeUnit.SECONDS.toNanos(1L), getHelpMessage(dropwizardName, timer));
    }

    /**
     * Export a Meter as as prometheus COUNTER.
     */
    MetricFamilySamples fromMeter(Metric metricName, Metered meter) {
        String dropwizardName = metricName.getKey();
        final MetricFamilySamples.Sample sample = sampleBuilder.createSample(metricName, "_total",
                new ArrayList<String>(),
                new ArrayList<String>(),
                meter.getCount());
        return new MetricFamilySamples(sample.name, Type.COUNTER, getHelpMessage(dropwizardName, meter),
                Arrays.asList(sample));
    }

    @Override
    public List<MetricFamilySamples> collect() {
        Map<String, MetricFamilySamples> mfSamplesMap = new HashMap<String, MetricFamilySamples>();

        for (Map.Entry<Metric, Gauge> entry : registry.getGauges(predicate).entrySet()) {
            addToMap(mfSamplesMap, fromGauge(entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<Metric, Counter> entry : registry.getCounters(predicate).entrySet()) {
            addToMap(mfSamplesMap, fromCounter(entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<Metric, Histogram> entry : registry.getHistograms(predicate).entrySet()) {
            addToMap(mfSamplesMap, fromHistogram(entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<Metric, Timer> entry : registry.getTimers(predicate).entrySet()) {
            addToMap(mfSamplesMap, fromTimer(entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<Metric, Metered> entry : registry.getMetereds(predicate).entrySet()) {
            addToMap(mfSamplesMap, fromMeter(entry.getKey(), entry.getValue()));
        }
        return new ArrayList<MetricFamilySamples>(mfSamplesMap.values());
    }

    private void addToMap(Map<String, MetricFamilySamples> mfSamplesMap, MetricFamilySamples newMfSamples) {
        if (newMfSamples != null) {
            MetricFamilySamples currentMfSamples = mfSamplesMap.get(newMfSamples.name);
            if (currentMfSamples == null) {
                mfSamplesMap.put(newMfSamples.name, newMfSamples);
            } else {
                List<MetricFamilySamples.Sample> samples = new ArrayList<MetricFamilySamples.Sample>(currentMfSamples.samples);
                samples.addAll(newMfSamples.samples);
                mfSamplesMap.put(newMfSamples.name, new MetricFamilySamples(newMfSamples.name, currentMfSamples.type, currentMfSamples.help, samples));
            }
        }
    }

    @Override
    public List<MetricFamilySamples> describe() {
        return new ArrayList<MetricFamilySamples>();
    }
}
