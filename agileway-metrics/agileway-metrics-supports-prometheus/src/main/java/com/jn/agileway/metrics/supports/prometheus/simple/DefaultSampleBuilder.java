package com.jn.agileway.metrics.supports.prometheus.simple;

import com.jn.agileway.metrics.core.MetricName;
import com.jn.agileway.metrics.core.tag.TagList;
import com.jn.langx.util.Objs;
import io.prometheus.client.Collector;

import java.util.ArrayList;
import java.util.List;


/**
 * Default implementation of {@link SampleBuilder}.
 * Sanitises the metric name if necessary.
 *
 * @see Collector#sanitizeMetricName(String)
 *
 * @since 4.1.0
 */
public class DefaultSampleBuilder implements SampleBuilder {
    @Override
    public Collector.MetricFamilySamples.Sample createSample(MetricName metricName, String nameSuffix, List<String> additionalLabelNames, List<String> additionalLabelValues, double value) {
        final String suffix = nameSuffix == null ? "" : nameSuffix;
        TagList tags = metricName.getTags();
        List<String> tagNames = Objs.isEmpty(tags) ? null : tags.getKeys();
        List<String> tagValues = Objs.isEmpty(tags) ? null : tags.getValues();
        final List<String> labelNames = additionalLabelNames == null ? tagNames : additionalLabelNames;
        final List<String> labelValues = additionalLabelValues == null ? tagValues : additionalLabelValues;
        return new Collector.MetricFamilySamples.Sample(
                Collector.sanitizeMetricName(metricName.getKey() + suffix),
                new ArrayList<String>(labelNames),
                new ArrayList<String>(labelValues),
                value
        );
    }
}
