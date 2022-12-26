package com.jn.agileway.metrics.supports.prometheus.simple;

import com.jn.agileway.metrics.core.MetricName;
import com.jn.agileway.metrics.core.tag.TagList;
import com.jn.langx.util.Maths;
import io.prometheus.client.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Custom {@link SampleBuilder} implementation to allow Dropwizard metrics to be translated to Prometheus metrics including custom labels and names.
 * Prometheus metric name and labels are extracted from the Dropwizard name based on the provided list of {@link MapperConfig}s.
 * The FIRST matching config will be used.
 * If no config is matched, the {@link DefaultSampleBuilder} is used.
 *
 * @since 4.1.0
 */
public class CustomMappingSampleBuilder implements SampleBuilder {
    private final List<CompiledMapperConfig> compiledMapperConfigs;
    private final DefaultSampleBuilder defaultMetricSampleBuilder = new DefaultSampleBuilder();

    public CustomMappingSampleBuilder(final List<MapperConfig> mapperConfigs) {
        if (mapperConfigs == null || mapperConfigs.isEmpty()) {
            throw new IllegalArgumentException("CustomMappingSampleBuilder needs some mapper configs!");
        }

        this.compiledMapperConfigs = new ArrayList<CompiledMapperConfig>(mapperConfigs.size());
        for (MapperConfig config : mapperConfigs) {
            this.compiledMapperConfigs.add(new CompiledMapperConfig(config));
        }
    }

    @Override
    public Collector.MetricFamilySamples.Sample createSample(final MetricName metricName, final String nameSuffix, final List<String> additionalLabelNames, final List<String> additionalLabelValues, final double value) {
        String agilewayName = metricName.getKey();
        if (agilewayName == null) {
            throw new IllegalArgumentException("Dropwizard metric name cannot be null");
        }

        CompiledMapperConfig matchingConfig = null;
        for (CompiledMapperConfig config : this.compiledMapperConfigs) {
            if (config.pattern.matches(agilewayName)) {
                matchingConfig = config;
                break;
            }
        }

        if (matchingConfig != null) {
            final Map<String, String> params = matchingConfig.pattern.extractParameters(agilewayName);
            TagList tagList = getTagList(matchingConfig.mapperConfig, params);
            int min = Maths.min(additionalLabelNames.size(), additionalLabelValues.size());
            for(int i=0; i< min;i++){
                tagList = tagList.and(additionalLabelNames.get(i), additionalLabelValues.get(i));
            }
            String newName =  getMetricName(matchingConfig.mapperConfig, params);
            MetricName newMetric= new MetricName(newName, metricName.getTags(), metricName.getMetricLevel());
            return defaultMetricSampleBuilder.createSample(
                    newMetric, nameSuffix,
                    tagList.getKeys(),
                    tagList.getValues(),
                    value
            );
        }

        return defaultMetricSampleBuilder.createSample(
                metricName, nameSuffix,
                additionalLabelNames,
                additionalLabelValues,
                value
        );
    }

    protected String getMetricName(final MapperConfig config, final Map<String, String> parameters){
        return formatTemplate(config.getName(), parameters);
    }

    protected TagList getTagList(final MapperConfig config, final Map<String, String> parameters) {
        TagList tags = new TagList();
        for (Map.Entry<String, String> entry : config.getLabels().entrySet()) {
            String key = entry.getKey();
            String value = formatTemplate(entry.getValue(), parameters);
            tags.and(key,value);
        }

        return tags;
    }



    private String formatTemplate(final String template, final Map<String, String> params) {
        String result = template;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        return result;
    }

    static class CompiledMapperConfig {
        final MapperConfig mapperConfig;
        final GraphiteNamePattern pattern;

        CompiledMapperConfig(final MapperConfig mapperConfig) {
            this.mapperConfig = mapperConfig;
            this.pattern = new GraphiteNamePattern(mapperConfig.getMatch());
        }
    }

    static class NameAndLabels {
        final String name;
        final List<String> labelNames;
        final List<String> labelValues;

        NameAndLabels(final String name, final List<String> labelNames, final List<String> labelValues) {
            this.name = name;
            this.labelNames = labelNames;
            this.labelValues = labelValues;
        }
    }
}
