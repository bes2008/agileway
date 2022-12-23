package com.jn.agileway.metrics.core.manager;

import com.jn.agileway.metrics.core.*;
import com.jn.agileway.metrics.core.meter.Timer;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meter.noop.*;
import com.jn.agileway.metrics.core.metricset.MetricRegistry;
import com.jn.agileway.metrics.core.metricset.MetricSet;
import com.jn.langx.util.Emptys;

import java.util.*;

/**
 * IMetricManager的空实现
 */
public class NoopMetricManager implements IMetricManager {

    private static final SortedSet emptySet = new TreeSet();

    private static final MetricRegistry NOP_REGISTRY = new MetricRegistry() {
        @Override
        public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException {
            return metric;
        }

        @Override
        public <T extends Metric> T register(MetricName name, T metric) throws IllegalArgumentException {
            return metric;
        }

        @Override
        public void registerAll(MetricSet metrics) throws IllegalArgumentException {

        }

        @Override
        public Counter counter(String name) {
            return NoopCounter.NOOP_COUNTER;
        }

        @Override
        public Counter counter(MetricName name) {
            return NoopCounter.NOOP_COUNTER;
        }

        @Override
        public Histogram histogram(MetricName name) {
            return NoopHistogram.NOOP_HISTOGRAM;
        }

        @Override
        public Histogram histogram(MetricName name, ReservoirType type) {
            return NoopHistogram.NOOP_HISTOGRAM;
        }

        @Override
        public Histogram histogram(String name) {
            return NoopHistogram.NOOP_HISTOGRAM;
        }

        @Override
        public Meter meter(MetricName name) {
            return NoopMeter.NOOP_METER;
        }

        @Override
        public Meter meter(String name) {
            return NoopMeter.NOOP_METER;
        }

        @Override
        public Timer timer(MetricName name) {
            return NoopTimer.NOOP_TIMER;
        }

        @Override
        public Timer timer(String name) {
            return NoopTimer.NOOP_TIMER;
        }

        @Override
        public Timer timer(MetricName name, ReservoirType type) {
            return NoopTimer.NOOP_TIMER;
        }

        @Override
        public Compass compass(MetricName name) {
            return NoopCompass.NOOP_COMPASS;
        }

        @Override
        public Compass compass(String name) {
            return NoopCompass.NOOP_COMPASS;
        }

        @Override
        public Compass compass(MetricName name, ReservoirType type) {
            return NoopCompass.NOOP_COMPASS;
        }

        @Override
        public FastCompass fastCompass(MetricName name) {
            return NoopFastCompass.NOOP_FAST_COMPASS;
        }

        @Override
        public ClusterHistogram clusterHistogram(MetricName name, long[] buckets) {
            return NoopClusterHistogram.NOOP_CLUSTER_HISTOGRAM;
        }

        @Override
        public boolean remove(MetricName name) {
            return false;
        }

        @Override
        public void removeMatching(MetricFilter filter) {

        }

        @Override
        public void addListener(MetricRegistryListener listener) {

        }

        @Override
        public void removeListener(MetricRegistryListener listener) {

        }

        @Override
        @SuppressWarnings("unchecked")
        public SortedSet<MetricName> getNames() {
            return emptySet;
        }

        @Override
        @SuppressWarnings("unchecked")
        public SortedMap<MetricName, Gauge> getGauges() {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        @SuppressWarnings("unchecked")
        public SortedMap<MetricName, Gauge> getGauges(MetricFilter filter) {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        @SuppressWarnings("unchecked")
        public SortedMap<MetricName, Counter> getCounters() {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        @SuppressWarnings("unchecked")
        public SortedMap<MetricName, Counter> getCounters(MetricFilter filter) {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        @SuppressWarnings("unchecked")
        public SortedMap<MetricName, Histogram> getHistograms() {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        @SuppressWarnings("unchecked")
        public SortedMap<MetricName, Histogram> getHistograms(MetricFilter filter) {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        @SuppressWarnings("unchecked")
        public SortedMap<MetricName, Meter> getMeters() {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        @SuppressWarnings("unchecked")
        public SortedMap<MetricName, Meter> getMeters(MetricFilter filter) {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        @SuppressWarnings("unchecked")
        public SortedMap<MetricName, Timer> getTimers() {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        @SuppressWarnings("unchecked")
        public SortedMap<MetricName, Timer> getTimers(MetricFilter filter) {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        @SuppressWarnings("unchecked")
        public SortedMap<MetricName, Compass> getCompasses(MetricFilter filter) {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        @SuppressWarnings("unchecked")
        public SortedMap<MetricName, Compass> getCompasses() {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        public SortedMap<MetricName, FastCompass> getFastCompasses() {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        public SortedMap<MetricName, FastCompass> getFastCompasses(MetricFilter filter) {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        public SortedMap<MetricName, ClusterHistogram> getClusterHistograms(MetricFilter filter) {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        @SuppressWarnings("unchecked")
        public SortedMap<MetricName, Metric> getMetrics(MetricFilter filter) {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Map<MetricName, Metric> getMetrics() {
            return Emptys.EMPTY_TREE_MAP;
        }

        @Override
        public long lastUpdateTime() {
            return 0;
        }

        @Override
        public SortedMap<MetricName, ClusterHistogram> getClusterHistograms() {
            return Emptys.EMPTY_TREE_MAP;
        }

    };

    @Override
    public Meter getMeter(String group, MetricName name) {
        return NoopMeter.NOOP_METER;
    }

    @Override
    public Counter getCounter(String group, MetricName name) {
        return NoopCounter.NOOP_COUNTER;
    }

    @Override
    public Histogram getHistogram(String group, MetricName name) {
        return NoopHistogram.NOOP_HISTOGRAM;
    }

    @Override
    public Histogram getHistogram(String group, MetricName name, ReservoirType type) {
        return NoopHistogram.NOOP_HISTOGRAM;
    }

    @Override
    public Timer getTimer(String group, MetricName name) {
        return NoopTimer.NOOP_TIMER;
    }

    @Override
    public Timer getTimer(String group, MetricName name, ReservoirType type) {
        return NoopTimer.NOOP_TIMER;
    }

    @Override
    public Compass getCompass(String group, MetricName name, ReservoirType type) {
        return NoopCompass.NOOP_COMPASS;
    }

    @Override
    public Compass getCompass(String group, MetricName name) {
        return NoopCompass.NOOP_COMPASS;
    }

    @Override
    public FastCompass getFastCompass(String group, MetricName name) {
        return NoopFastCompass.NOOP_FAST_COMPASS;
    }

    @Override
    public ClusterHistogram getClusterHistogram(String group, MetricName name, long[] buckets) {
        return NoopClusterHistogram.NOOP_CLUSTER_HISTOGRAM;
    }

    @Override
    public List<String> listMetricGroups() {
        return Collections.emptyList();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public Map<String, Set<MetricName>> listMetricNamesByGroup() {
        return Collections.emptyMap();
    }

    @Override
    public MetricRegistry getMetricRegistryByGroup(String group) {
        return NOP_REGISTRY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<MetricName, Gauge> getGauges(String group, MetricFilter filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<MetricName, Counter> getCounters(String group, MetricFilter filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<MetricName, Histogram> getHistograms(String group, MetricFilter filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<MetricName, Meter> getMeters(String group, MetricFilter filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<MetricName, Timer> getTimers(String group, MetricFilter filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<MetricName, Compass> getCompasses(String group, MetricFilter filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<MetricName, FastCompass> getFastCompasses(String group, MetricFilter filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public void register(String group, MetricName name, Metric metric) {

    }

    @Override
    public Map<MetricName, Metric> getMetrics(String group) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> getCategoryMetrics(String group) {
        return getCategoryMetrics(group, MetricFilter.ALL);
    }

    @Override
    public Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> getCategoryMetrics(String group,
                                                                                              MetricFilter filter) {
        Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> result = new HashMap<Class<? extends Metric>, Map<MetricName, ? extends Metric>>();

        Map<MetricName, Gauge> gauges = Collections.emptyMap();
        Map<MetricName, Counter> counters = Collections.emptyMap();
        Map<MetricName, Histogram> histograms = Collections.emptyMap();
        Map<MetricName, Meter> meters = Collections.emptyMap();
        Map<MetricName, Timer> timers = Collections.emptyMap();
        Map<MetricName, Compass> compasses = Collections.emptyMap();
        Map<MetricName, FastCompass> fastCompasses = Collections.emptyMap();
        Map<MetricName, ClusterHistogram> clusterHistogrames = Collections.emptyMap();

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

    @Override
    public void clear() {

    }

    @Override
    public Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> getAllCategoryMetrics(MetricFilter filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public SortedMap<MetricName, ClusterHistogram> getClusterHistogram(String group, MetricFilter filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

}
