package com.jn.agileway.metrics.supports.prometheus.simple;

import com.jn.agileway.metrics.core.Metric;
import io.prometheus.client.Collector;

import java.util.List;


/**
 * SampleBuilder defines the action of creating a {@link Collector.MetricFamilySamples.Sample} for the given parameters.
 *
 * @since 4.1.0
 */
public interface SampleBuilder {

    /**
     * Creates a new {@link Collector.MetricFamilySamples.Sample} for the given parameters.
     *
     * @param nameSuffix            Optional suffix to add.
     * @param value                 Metric value
     * @return A new {@link Collector.MetricFamilySamples.Sample}.
     */
    Collector.MetricFamilySamples.Sample createSample(Metric metricName, String nameSuffix, List<String> additionalLabelNames, List<String> additionalLabelValues, double value);
}
