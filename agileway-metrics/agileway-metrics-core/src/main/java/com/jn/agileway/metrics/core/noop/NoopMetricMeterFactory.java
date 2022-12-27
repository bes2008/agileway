package com.jn.agileway.metrics.core.noop;

import com.jn.agileway.metrics.core.*;
import com.jn.agileway.metrics.core.predicate.FixedPredicate;
import com.jn.agileway.metrics.core.predicate.MetricMeterPredicate;
import com.jn.agileway.metrics.core.meterset.MetricMeterFactory;
import com.jn.agileway.metrics.core.meter.Timer;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meter.impl.ClusterHistogram;
import com.jn.agileway.metrics.core.meterset.MetricMeterRegistry;
import com.jn.agileway.metrics.core.snapshot.ReservoirType;
import com.jn.langx.util.Emptys;

import java.util.*;

/**
 * @since 4.1.0
 */
public class NoopMetricMeterFactory implements MetricMeterFactory {


    @Override
    public Metered getMeter(String group, Metric name) {
        return NoopMetered.NOOP_METER;
    }

    @Override
    public Counter getCounter(String group, Metric name) {
        return NoopCounter.NOOP_COUNTER;
    }

    @Override
    public Histogram getHistogram(String group, Metric name) {
        return NoopHistogram.NOOP_HISTOGRAM;
    }

    @Override
    public Histogram getHistogram(String group, Metric name, ReservoirType type) {
        return NoopHistogram.NOOP_HISTOGRAM;
    }

    @Override
    public Timer getTimer(String group, Metric name) {
        return NoopTimer.NOOP_TIMER;
    }

    @Override
    public Timer getTimer(String group, Metric name, ReservoirType type) {
        return NoopTimer.NOOP_TIMER;
    }

    @Override
    public Compass getCompass(String group, Metric name, ReservoirType type) {
        return NoopCompass.NOOP_COMPASS;
    }

    @Override
    public Compass getCompass(String group, Metric name) {
        return NoopCompass.NOOP_COMPASS;
    }

    @Override
    public FastCompass getFastCompass(String group, Metric name) {
        return NoopFastCompass.NOOP_FAST_COMPASS;
    }

    @Override
    public ClusterHistogram getClusterHistogram(String group, Metric name, long[] buckets) {
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
    public Map<String, Set<Metric>> listMetricNamesByGroup() {
        return Collections.emptyMap();
    }

    @Override
    public MetricMeterRegistry getMetricRegistryByGroup(String group) {
        return NoopMetricMeterRegistry.NOOP_METRIC_REGISTRY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<Metric, Gauge> getGauges(String group, MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<Metric, Counter> getCounters(String group, MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<Metric, Histogram> getHistograms(String group, MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<Metric, Metered> getMeters(String group, MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<Metric, Timer> getTimers(String group, MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<Metric, Compass> getCompasses(String group, MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<Metric, FastCompass> getFastCompasses(String group, MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public void register(String group, Metric name, Meter metric) {

    }

    @Override
    public Map<Metric, Meter> getMetrics(String group) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Class<? extends Meter>, Map<Metric, ? extends Meter>> getCategoryMetrics(String group) {
        return getCategoryMetrics(group, FixedPredicate.TRUE);
    }

    @Override
    public Map<Class<? extends Meter>, Map<Metric, ? extends Meter>> getCategoryMetrics(String group,
                                                                                        MetricMeterPredicate filter) {
        Map<Class<? extends Meter>, Map<Metric, ? extends Meter>> result = new HashMap<Class<? extends Meter>, Map<Metric, ? extends Meter>>();

        Map<Metric, Gauge> gauges = Collections.emptyMap();
        Map<Metric, Counter> counters = Collections.emptyMap();
        Map<Metric, Histogram> histograms = Collections.emptyMap();
        Map<Metric, Metered> meters = Collections.emptyMap();
        Map<Metric, Timer> timers = Collections.emptyMap();
        Map<Metric, Compass> compasses = Collections.emptyMap();
        Map<Metric, FastCompass> fastCompasses = Collections.emptyMap();
        Map<Metric, ClusterHistogram> clusterHistogrames = Collections.emptyMap();

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

    @Override
    public void clear() {

    }

    @Override
    public Map<Class<? extends Meter>, Map<Metric, ? extends Meter>> getAllCategoryMetrics(MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public SortedMap<Metric, ClusterHistogram> getClusterHistogram(String group, MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

}
