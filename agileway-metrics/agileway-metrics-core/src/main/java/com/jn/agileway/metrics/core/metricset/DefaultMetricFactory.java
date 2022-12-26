package com.jn.agileway.metrics.core.metricset;

import com.jn.agileway.metrics.core.*;
import com.jn.agileway.metrics.core.predicate.FixedPredicate;
import com.jn.agileway.metrics.core.predicate.MetricPredicate;
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
public class DefaultMetricFactory implements MetricFactory {

    /**
     * key: group
     * value: registry
     */
    private Map<String, MetricRegistry> metricRegistryMap;

    private volatile boolean enabled;

    public DefaultMetricFactory() {
        metricRegistryMap = new ConcurrentHashMap<String, MetricRegistry>();
        enabled = true;
    }

    @Override
    public Meter getMeter(String group, MetricName name) {
        if (!this.enabled) {
            return Metrics.NOOP_METRIC_FACTORY.getMeter(group, name);
        }
        return getMetricRegistryByGroup(group).meter(name);
    }

    @Override
    public Counter getCounter(String group, MetricName name) {
        if (!this.enabled) {
            return Metrics.NOOP_METRIC_FACTORY.getCounter(group, name);
        }
        return getMetricRegistryByGroup(group).counter(name);
    }

    @Override
    public Histogram getHistogram(String group, MetricName name) {
        if (!this.enabled) {
            return Metrics.NOOP_METRIC_FACTORY.getHistogram(group, name);
        }
        return getMetricRegistryByGroup(group).histogram(name);
    }

    @Override
    public Histogram getHistogram(String group, MetricName name, ReservoirType type) {
        if (!this.enabled) {
            return Metrics.NOOP_METRIC_FACTORY.getHistogram(group, name, type);
        }
        return getMetricRegistryByGroup(group).histogram(name, type);
    }

    @Override
    public Timer getTimer(String group, MetricName name) {
        if (!this.enabled) {
            return Metrics.NOOP_METRIC_FACTORY.getTimer(group, name);
        }
        return getMetricRegistryByGroup(group).timer(name);
    }

    @Override
    public Timer getTimer(String group, MetricName name, ReservoirType type) {
        if (!this.enabled) {
            return Metrics.NOOP_METRIC_FACTORY.getTimer(group, name, type);
        }
        return getMetricRegistryByGroup(group).timer(name, type);
    }

    @Override
    public Compass getCompass(String group, MetricName name) {
        if (!this.enabled) {
            return Metrics.NOOP_METRIC_FACTORY.getCompass(group, name);
        }
        return getMetricRegistryByGroup(group).compass(name);
    }

    @Override
    public Compass getCompass(String group, MetricName name, ReservoirType type) {
        if (!this.enabled) {
            return Metrics.NOOP_METRIC_FACTORY.getCompass(group, name, type);
        }
        return getMetricRegistryByGroup(group).compass(name, type);
    }

    @Override
    public FastCompass getFastCompass(String group, MetricName name) {
        if (!this.enabled) {
            return Metrics.NOOP_METRIC_FACTORY.getFastCompass(group, name);
        }
        return getMetricRegistryByGroup(group).fastCompass(name);
    }

    @Override
    public ClusterHistogram getClusterHistogram(String group, MetricName name, long[] buckets) {
        if (!this.enabled) {
            return Metrics.NOOP_METRIC_FACTORY.getClusterHistogram(group, name, buckets);
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
    public Map<String, Set<MetricName>> listMetricNamesByGroup() {
        if (!this.enabled) {
            return Collections.emptyMap();
        }
        Map<String, Set<MetricName>> result = new HashMap<String, Set<MetricName>>();
        for (Map.Entry<String, MetricRegistry> entry : metricRegistryMap.entrySet()) {
            Set<MetricName> metricNames = new TreeSet<MetricName>();
            for (Map.Entry<MetricName, Metric> metricEntry : entry.getValue().getMetrics().entrySet()) {
                metricNames.add(metricEntry.getKey());
            }
            result.put(entry.getKey(), metricNames);
        }
        return result;
    }

    @Override
    public MetricRegistry getMetricRegistryByGroup(String group) {
        if (!metricRegistryMap.containsKey(group)) {
            metricRegistryMap.put(group, new DefaultMetricRegistry());
        }
        return metricRegistryMap.get(group);
    }

    @Override
    public Map<MetricName, Gauge> getGauges(String group, MetricPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }
        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getGauges(filter);
    }

    @Override
    public Map<MetricName, Counter> getCounters(String group, MetricPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getCounters(filter);
    }

    @Override
    public Map<MetricName, Histogram> getHistograms(String group, MetricPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getHistograms(filter);
    }

    @Override
    public Map<MetricName, Meter> getMeters(String group, MetricPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getMeters(filter);
    }

    @Override
    public Map<MetricName, Timer> getTimers(String group, MetricPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getTimers(filter);
    }

    @Override
    public Map<MetricName, Compass> getCompasses(String group, MetricPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getCompasses(filter);
    }

    @Override
    public Map<MetricName, FastCompass> getFastCompasses(String group, MetricPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getFastCompasses(filter);
    }

    @Override
    public Map<MetricName, ClusterHistogram> getClusterHistogram(String group, MetricPredicate filter) {
        if (!this.enabled) {
            return Emptys.EMPTY_TREE_MAP;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return Emptys.EMPTY_TREE_MAP;
        }
        return metricRegistry.getClusterHistograms(filter);
    }

    @Override
    public void register(String group, MetricName name, Metric metric) {
        if (!this.enabled) {
            return;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        metricRegistry.register(name, metric);
    }

    @Override
    public Map<MetricName, Metric> getMetrics(String group) {
        if (!this.enabled) {
            return Collections.emptyMap();
        }

        MetricRegistry metricRegistry = this.metricRegistryMap.get(group);
        if (metricRegistry != null) {
            return metricRegistry.getMetrics();
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> getCategoryMetrics(String group) {
        return getCategoryMetrics(group, FixedPredicate.TRUE);
    }

    @Override
    public Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> getCategoryMetrics(String group,
                                                                                              MetricPredicate filter) {
        if (!this.enabled) {
            return Collections.emptyMap();
        }

        MetricRegistry metricRegistry = this.metricRegistryMap.get(group);
        Map<MetricName, Metric> metrics = metricRegistry.getMetrics();
        return getCategoryMetrics(metrics, filter);
    }

    @Override
    public Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> getAllCategoryMetrics(MetricPredicate filter) {

        if (!this.enabled) {
            return Collections.emptyMap();
        }

        Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> result = new HashMap<Class<? extends Metric>, Map<MetricName, ? extends Metric>>();

        Map<MetricName, Gauge> gauges = new HashMap<MetricName, Gauge>();
        Map<MetricName, Counter> counters = new HashMap<MetricName, Counter>();
        Map<MetricName, Histogram> histograms = new HashMap<MetricName, Histogram>();
        Map<MetricName, Meter> meters = new HashMap<MetricName, Meter>();
        Map<MetricName, Timer> timers = new HashMap<MetricName, Timer>();
        Map<MetricName, Compass> compasses = new HashMap<MetricName, Compass>();
        Map<MetricName, FastCompass> fastCompasses = new HashMap<MetricName, FastCompass>();
        Map<MetricName, ClusterHistogram> clusterHistogrames = new HashMap<MetricName, ClusterHistogram>();

        for (Entry<String, MetricRegistry> entry : metricRegistryMap.entrySet()) {

            MetricRegistry metricRegistry = entry.getValue();

            Map<MetricName, Metric> metrics = metricRegistry.getMetrics();

            for (Entry<MetricName, Metric> entry1 : metrics.entrySet()) {

                checkAndAdd(entry1, filter, gauges, counters, histograms, meters, timers, compasses, fastCompasses, clusterHistogrames);

            }
        }

        result.put(Gauge.class, gauges);
        result.put(Counter.class, counters);
        result.put(Histogram.class, histograms);
        result.put(Meter.class, meters);
        result.put(Timer.class, timers);
        result.put(Compass.class, compasses);
        result.put(FastCompass.class, fastCompasses);
        result.put(ClusterHistogram.class, clusterHistogrames);

        return result;
    }

    private Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> getCategoryMetrics(
            Map<MetricName, Metric> metrics, MetricPredicate filter) {
        if (!this.enabled) {
            return Collections.emptyMap();
        }

        Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> result = new HashMap<Class<? extends Metric>, Map<MetricName, ? extends Metric>>();

        Map<MetricName, Gauge> gauges = new HashMap<MetricName, Gauge>();
        Map<MetricName, Counter> counters = new HashMap<MetricName, Counter>();
        Map<MetricName, Histogram> histograms = new HashMap<MetricName, Histogram>();
        Map<MetricName, Meter> meters = new HashMap<MetricName, Meter>();
        Map<MetricName, Timer> timers = new HashMap<MetricName, Timer>();
        Map<MetricName, Compass> compasses = new HashMap<MetricName, Compass>();
        Map<MetricName, FastCompass> fastCompasses = new HashMap<MetricName, FastCompass>();
        Map<MetricName, ClusterHistogram> clusterHistogrames = new HashMap<MetricName, ClusterHistogram>();

        for (Map.Entry<MetricName, Metric> entry : metrics.entrySet()) {
            checkAndAdd(entry, filter, gauges, counters, histograms, meters, timers, compasses, fastCompasses, clusterHistogrames);
        }

        result.put(Gauge.class, gauges);
        result.put(Counter.class, counters);
        result.put(Histogram.class, histograms);
        result.put(Meter.class, meters);
        result.put(Timer.class, timers);
        result.put(Compass.class, compasses);
        result.put(FastCompass.class, fastCompasses);
        result.put(ClusterHistogram.class, clusterHistogrames);

        return result;
    }

    private void checkAndAdd(Map.Entry<MetricName, Metric> entry, MetricPredicate predicate, Map<MetricName, Gauge> gauges,
                             Map<MetricName, Counter> counters, Map<MetricName, Histogram> histograms, Map<MetricName, Meter> meters,
                             Map<MetricName, Timer> timers, Map<MetricName, Compass> compasses, Map<MetricName, FastCompass> fastCompasses, Map<MetricName, ClusterHistogram> clusterHistogrames) {

        MetricName metricName = entry.getKey();
        Metric metric = entry.getValue();
        if (metric instanceof Gauge && predicate.test(metricName, metric)) {
            gauges.put(metricName, (Gauge) metric);
        } else if (metric instanceof Counter && predicate.test(metricName, metric)) {
            counters.put(metricName, (Counter) metric);
        } else if (metric instanceof Histogram && predicate.test(metricName, metric)) {
            histograms.put(metricName, (Histogram) metric);
        } else if (metric instanceof Meter && predicate.test(metricName, metric)) {
            meters.put(metricName, (Meter) metric);
        } else if (metric instanceof Timer && predicate.test(metricName, metric)) {
            timers.put(metricName, (Timer) metric);
        } else if (metric instanceof Compass && predicate.test(metricName, metric)) {
            compasses.put(metricName, (Compass) metric);
        } else if (metric instanceof FastCompass && predicate.test(metricName, metric)) {
            fastCompasses.put(metricName, (FastCompass) metric);
        } else if (metric instanceof ClusterHistogram && predicate.test(metricName, metric)) {
            clusterHistogrames.put(metricName, (ClusterHistogram) metric);
        } else if (metric instanceof DynamicMetricSet) {
            DynamicMetricSet dynamicMetricSet = (DynamicMetricSet) metric;
            Map<MetricName, Metric> dynamicMetrics = dynamicMetricSet.getDynamicMetrics();
            for (Map.Entry<MetricName, Metric> dynamicMetricEntry : dynamicMetrics.entrySet()) {
                checkAndAdd(dynamicMetricEntry, predicate, gauges, counters, histograms, meters, timers, compasses, fastCompasses, clusterHistogrames);
            }
        }
    }

    @Override
    public void clear() {
        metricRegistryMap.clear();
    }

}
