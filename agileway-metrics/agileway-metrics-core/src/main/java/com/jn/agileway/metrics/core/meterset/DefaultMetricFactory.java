package com.jn.agileway.metrics.core.meterset;

import com.jn.agileway.metrics.core.*;
import com.jn.agileway.metrics.core.predicate.FixedPredicate;
import com.jn.agileway.metrics.core.predicate.MetricMeterPredicate;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meter.Timer;
import com.jn.agileway.metrics.core.meter.impl.ClusterHistogram;
import com.jn.agileway.metrics.core.snapshot.ReservoirType;
import com.jn.langx.util.Emptys;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @since 4.1.0
 */
public class DefaultMetricFactory implements MetricMeterFactory {

    /**
     * key: group
     * value: registry
     */
    private Map<String, MetricMeterRegistry> metricRegistryMap;

    private volatile boolean enabled;

    public DefaultMetricFactory() {
        metricRegistryMap = new ConcurrentHashMap<String, MetricMeterRegistry>();
        enabled = true;
    }

    @Override
    public Metered getMeter(String group, Metric name) {
        if (!this.enabled) {
            return Meters.NOOP_METRIC_FACTORY.getMeter(group, name);
        }
        return getMetricRegistryByGroup(group).metered(name);
    }

    @Override
    public Counter getCounter(String group, Metric name) {
        if (!this.enabled) {
            return Meters.NOOP_METRIC_FACTORY.getCounter(group, name);
        }
        return getMetricRegistryByGroup(group).counter(name);
    }

    @Override
    public Histogram getHistogram(String group, Metric name) {
        if (!this.enabled) {
            return Meters.NOOP_METRIC_FACTORY.getHistogram(group, name);
        }
        return getMetricRegistryByGroup(group).histogram(name);
    }

    @Override
    public Histogram getHistogram(String group, Metric name, ReservoirType type) {
        if (!this.enabled) {
            return Meters.NOOP_METRIC_FACTORY.getHistogram(group, name, type);
        }
        return getMetricRegistryByGroup(group).histogram(name, type);
    }

    @Override
    public Timer getTimer(String group, Metric name) {
        if (!this.enabled) {
            return Meters.NOOP_METRIC_FACTORY.getTimer(group, name);
        }
        return getMetricRegistryByGroup(group).timer(name);
    }

    @Override
    public Timer getTimer(String group, Metric name, ReservoirType type) {
        if (!this.enabled) {
            return Meters.NOOP_METRIC_FACTORY.getTimer(group, name, type);
        }
        return getMetricRegistryByGroup(group).timer(name, type);
    }

    @Override
    public Compass getCompass(String group, Metric name) {
        if (!this.enabled) {
            return Meters.NOOP_METRIC_FACTORY.getCompass(group, name);
        }
        return getMetricRegistryByGroup(group).compass(name);
    }

    @Override
    public Compass getCompass(String group, Metric name, ReservoirType type) {
        if (!this.enabled) {
            return Meters.NOOP_METRIC_FACTORY.getCompass(group, name, type);
        }
        return getMetricRegistryByGroup(group).compass(name, type);
    }

    @Override
    public FastCompass getFastCompass(String group, Metric name) {
        if (!this.enabled) {
            return Meters.NOOP_METRIC_FACTORY.getFastCompass(group, name);
        }
        return getMetricRegistryByGroup(group).fastCompass(name);
    }

    @Override
    public ClusterHistogram getClusterHistogram(String group, Metric name, long[] buckets) {
        if (!this.enabled) {
            return Meters.NOOP_METRIC_FACTORY.getClusterHistogram(group, name, buckets);
        }
        return getMetricRegistryByGroup(group).clusterHistogram(name, buckets);
    }

    @Override
    public List<String> listMetricGroups() {
        if (!this.enabled) {
            return Collections.emptyList();
        }
        List<String> groups = new ArrayList<String>();
        groups.addAll(metricRegistryMap.keySet());
        return groups;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Map<String, Set<Metric>> listMetricNamesByGroup() {
        if (!this.enabled) {
            return Collections.emptyMap();
        }
        Map<String, Set<Metric>> result = new HashMap<String, Set<Metric>>();
        for (Map.Entry<String, MetricMeterRegistry> entry : metricRegistryMap.entrySet()) {
            Set<Metric> metricNames = new TreeSet<Metric>();
            for (Map.Entry<Metric, Meter> metricEntry : entry.getValue().getMetricMeters().entrySet()) {
                metricNames.add(metricEntry.getKey());
            }
            result.put(entry.getKey(), metricNames);
        }
        return result;
    }

    @Override
    public MetricMeterRegistry getMetricRegistryByGroup(String group) {
        if (!metricRegistryMap.containsKey(group)) {
            metricRegistryMap.put(group, new DefaultMetricRegistry());
        }
        return metricRegistryMap.get(group);
    }

    @Override
    public Map<Metric, Gauge> getGauges(String group, MetricMeterPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }
        MetricMeterRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getGauges(filter);
    }

    @Override
    public Map<Metric, Counter> getCounters(String group, MetricMeterPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }

        MetricMeterRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getCounters(filter);
    }

    @Override
    public Map<Metric, Histogram> getHistograms(String group, MetricMeterPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }

        MetricMeterRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getHistograms(filter);
    }

    @Override
    public Map<Metric, Metered> getMetereds(String group, MetricMeterPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }

        MetricMeterRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getMetereds(filter);
    }

    @Override
    public Map<Metric, Timer> getTimers(String group, MetricMeterPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }

        MetricMeterRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getTimers(filter);
    }

    @Override
    public Map<Metric, Compass> getCompasses(String group, MetricMeterPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }

        MetricMeterRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getCompasses(filter);
    }

    @Override
    public Map<Metric, FastCompass> getFastCompasses(String group, MetricMeterPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }

        MetricMeterRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getFastCompasses(filter);
    }

    @Override
    public Map<Metric, ClusterHistogram> getClusterHistogram(String group, MetricMeterPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }

        MetricMeterRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getClusterHistograms(filter);
    }

    @Override
    public void register(String group, Metric name, Meter metric) {
        if (!this.enabled) {
            return;
        }

        MetricMeterRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        metricRegistry.register(name, metric);
    }

    @Override
    public Map<Metric, Meter> getMetrics(String group) {
        if (!this.enabled) {
            return Collections.emptyMap();
        }

        MetricMeterRegistry metricRegistry = this.metricRegistryMap.get(group);
        if (metricRegistry != null) {
            return metricRegistry.getMetricMeters();
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<Class<? extends Meter>, Map<Metric, ? extends Meter>> getCategoryMetrics(String group) {
        return getCategoryMetrics(group, FixedPredicate.TRUE);
    }

    @Override
    public Map<Class<? extends Meter>, Map<Metric, ? extends Meter>> getCategoryMetrics(String group,
                                                                                        MetricMeterPredicate filter) {
        if (!this.enabled) {
            return Collections.emptyMap();
        }

        MetricMeterRegistry metricRegistry = this.metricRegistryMap.get(group);
        Map<Metric, Meter> metrics = metricRegistry.getMetricMeters();
        return getCategoryMetrics(metrics, filter);
    }

    @Override
    public Map<Class<? extends Meter>, Map<Metric, ? extends Meter>> getAllCategoryMetrics(MetricMeterPredicate filter) {

        if (!this.enabled) {
            return Collections.emptyMap();
        }

        Map<Class<? extends Meter>, Map<Metric, ? extends Meter>> result = new HashMap<Class<? extends Meter>, Map<Metric, ? extends Meter>>();

        Map<Metric, Gauge> gauges = new HashMap<Metric, Gauge>();
        Map<Metric, Counter> counters = new HashMap<Metric, Counter>();
        Map<Metric, Histogram> histograms = new HashMap<Metric, Histogram>();
        Map<Metric, Metered> meters = new HashMap<Metric, Metered>();
        Map<Metric, Timer> timers = new HashMap<Metric, Timer>();
        Map<Metric, Compass> compasses = new HashMap<Metric, Compass>();
        Map<Metric, FastCompass> fastCompasses = new HashMap<Metric, FastCompass>();
        Map<Metric, ClusterHistogram> clusterHistogrames = new HashMap<Metric, ClusterHistogram>();

        for (Entry<String, MetricMeterRegistry> entry : metricRegistryMap.entrySet()) {

            MetricMeterRegistry metricRegistry = entry.getValue();

            Map<Metric, Meter> metrics = metricRegistry.getMetricMeters();

            for (Entry<Metric, Meter> entry1 : metrics.entrySet()) {

                checkAndAdd(entry1, filter, gauges, counters, histograms, meters, timers, compasses, fastCompasses, clusterHistogrames);

            }
        }

        result.put(Gauge.class, gauges);
        result.put(Counter.class, counters);
        result.put(Histogram.class, histograms);
        result.put(Metered.class, meters);
        result.put(Timer.class, timers);
        result.put(Compass.class, compasses);
        result.put(FastCompass.class, fastCompasses);
        result.put(ClusterHistogram.class, clusterHistogrames);

        return result;
    }

    private Map<Class<? extends Meter>, Map<Metric, ? extends Meter>> getCategoryMetrics(
            Map<Metric, Meter> metrics, MetricMeterPredicate filter) {
        if (!this.enabled) {
            return Collections.emptyMap();
        }

        Map<Class<? extends Meter>, Map<Metric, ? extends Meter>> result = new HashMap<Class<? extends Meter>, Map<Metric, ? extends Meter>>();

        Map<Metric, Gauge> gauges = new HashMap<Metric, Gauge>();
        Map<Metric, Counter> counters = new HashMap<Metric, Counter>();
        Map<Metric, Histogram> histograms = new HashMap<Metric, Histogram>();
        Map<Metric, Metered> meters = new HashMap<Metric, Metered>();
        Map<Metric, Timer> timers = new HashMap<Metric, Timer>();
        Map<Metric, Compass> compasses = new HashMap<Metric, Compass>();
        Map<Metric, FastCompass> fastCompasses = new HashMap<Metric, FastCompass>();
        Map<Metric, ClusterHistogram> clusterHistogrames = new HashMap<Metric, ClusterHistogram>();

        for (Map.Entry<Metric, Meter> entry : metrics.entrySet()) {
            checkAndAdd(entry, filter, gauges, counters, histograms, meters, timers, compasses, fastCompasses, clusterHistogrames);
        }

        result.put(Gauge.class, gauges);
        result.put(Counter.class, counters);
        result.put(Histogram.class, histograms);
        result.put(Metered.class, meters);
        result.put(Timer.class, timers);
        result.put(Compass.class, compasses);
        result.put(FastCompass.class, fastCompasses);
        result.put(ClusterHistogram.class, clusterHistogrames);

        return result;
    }

    private void checkAndAdd(Map.Entry<Metric, Meter> entry, MetricMeterPredicate predicate, Map<Metric, Gauge> gauges,
                             Map<Metric, Counter> counters, Map<Metric, Histogram> histograms, Map<Metric, Metered> meters,
                             Map<Metric, Timer> timers, Map<Metric, Compass> compasses, Map<Metric, FastCompass> fastCompasses, Map<Metric, ClusterHistogram> clusterHistogrames) {

        Metric metricName = entry.getKey();
        Meter metric = entry.getValue();
        if (metric instanceof Gauge && predicate.test(metricName, metric)) {
            gauges.put(metricName, (Gauge) metric);
        } else if (metric instanceof Counter && predicate.test(metricName, metric)) {
            counters.put(metricName, (Counter) metric);
        } else if (metric instanceof Histogram && predicate.test(metricName, metric)) {
            histograms.put(metricName, (Histogram) metric);
        } else if (metric instanceof Metered && predicate.test(metricName, metric)) {
            meters.put(metricName, (Metered) metric);
        } else if (metric instanceof Timer && predicate.test(metricName, metric)) {
            timers.put(metricName, (Timer) metric);
        } else if (metric instanceof Compass && predicate.test(metricName, metric)) {
            compasses.put(metricName, (Compass) metric);
        } else if (metric instanceof FastCompass && predicate.test(metricName, metric)) {
            fastCompasses.put(metricName, (FastCompass) metric);
        } else if (metric instanceof ClusterHistogram && predicate.test(metricName, metric)) {
            clusterHistogrames.put(metricName, (ClusterHistogram) metric);
        } else if (metric instanceof DynamicMetricMeterSet) {
            DynamicMetricMeterSet dynamicMetricSet = (DynamicMetricMeterSet) metric;
            Map<Metric, Meter> dynamicMetrics = dynamicMetricSet.getDynamicMetrics();
            for (Map.Entry<Metric, Meter> dynamicMetricEntry : dynamicMetrics.entrySet()) {
                checkAndAdd(dynamicMetricEntry, predicate, gauges, counters, histograms, meters, timers, compasses, fastCompasses, clusterHistogrames);
            }
        }
    }

    @Override
    public void clear() {
        metricRegistryMap.clear();
    }

}
