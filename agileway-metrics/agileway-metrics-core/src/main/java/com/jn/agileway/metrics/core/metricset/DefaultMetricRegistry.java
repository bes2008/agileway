package com.jn.agileway.metrics.core.metricset;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.MetricName;
import com.jn.agileway.metrics.core.config.MetricsCollectPeriodConfig;
import com.jn.agileway.metrics.core.meter.Timer;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meter.impl.*;
import com.jn.agileway.metrics.core.noop.*;
import com.jn.agileway.metrics.core.predicate.FixedPredicate;
import com.jn.agileway.metrics.core.predicate.MetricPredicate;
import com.jn.agileway.metrics.core.snapshot.ReservoirType;
import com.jn.agileway.metrics.core.snapshot.ReservoirTypeBuilder;
import com.jn.agileway.metrics.core.snapshot.ReservoirTypeMetricBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A registry of metric instances.
 *
 * @since 4.1.0
 */
public class DefaultMetricRegistry implements MetricRegistry {

    private static final int DEFAULT_MAX_METRIC_COUNT = Integer.getInteger("com.jn.agileway.metrics.core.maxMetricCountPerRegistry", 50000);

    // 用于分桶计数统计间隔配置
    private static final MetricsCollectPeriodConfig config = new MetricsCollectPeriodConfig();

    private final ConcurrentMap<MetricName, Meter> metrics;
    private final List<MetricRegistryListener> listeners;
    private final int maxMetricCount;
    /**
     * A quick and easy way of capturing the notion of default metrics.
     */
    private CounterBuilder COUNTER_BUILDER = new CounterBuilder();

    private HistogramBuilder HISTOGRAM_BUILDER = new HistogramBuilder();

    private MeteredBuilder METER_BUILDER = new MeteredBuilder();
    private TimerBuilder TIMER_BUILDER = new TimerBuilder();
    private CompassBuilder COMPASS_BUILDER = new CompassBuilder();

    private FastCompassBuilder FAST_COMPASS_BUILDER = new FastCompassBuilder();

    private ClusterHistogramBuilder CLUSTER_HISTOGRAM_BUILDER = new ClusterHistogramBuilder();

    /**
     * Creates a new {@link MetricRegistry}.
     */
    public DefaultMetricRegistry() {
        this(DEFAULT_MAX_METRIC_COUNT);
    }

    public DefaultMetricRegistry(int maxMetricCount) {
        this.metrics = new ConcurrentHashMap<MetricName, Meter>();
        this.listeners = new CopyOnWriteArrayList<MetricRegistryListener>();
        this.maxMetricCount = maxMetricCount;
    }

    /**
     * @see #register(MetricName, Meter)
     */
    public <T extends Meter> T register(String name, T metric) throws IllegalArgumentException {
        return register(MetricName.build(name), metric);
    }

    /**
     * 〈unRegister all metrics.〉
     * <p>
     * eg:
     * When collecting remote machine JVM metrics through the JMX port, May be due to network jitter or
     * remote process restart, Need to cancel the previously collected metrics and re-register.
     */
    public void unregisterAll() {
        metrics.clear();
    }

    /**
     * Given a {@link Meter}, registers it under the given name.
     *
     * @param name   the name of the metric
     * @param metric the metric
     * @param <T>    the type of the metric
     * @return {@code metric}
     * @throws IllegalArgumentException if the name is already registered
     */
    public <T extends Meter> T register(MetricName name, T metric) throws IllegalArgumentException {
        if (metric instanceof MetricSet && !(metric instanceof DynamicMetricSet)) {
            registerAll(name, (MetricSet) metric);
        } else {
            final Meter existing = metrics.putIfAbsent(name, metric);
            if (existing == null) {
                onMetricAdded(name, metric);
            } else {
                throw new IllegalArgumentException("A metric named " + name + " already exists");
            }
        }
        return metric;
    }

    /**
     * Given a metric set, registers them.
     *
     * @param metrics a set of metrics
     * @throws IllegalArgumentException if any of the names are already registered
     */
    public void registerAll(MetricSet metrics) throws IllegalArgumentException {
        registerAll(null, metrics);
    }

    /**
     * @see #counter(MetricName)
     */
    public Counter counter(String name) {
        return counter(MetricName.build(name));
    }

    /**
     * Return the {@link Counter} registered under this name; or create and register
     * a new {@link Counter} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Counter}
     */
    public Counter counter(MetricName name) {
        Counter counter = getOrAdd(name, COUNTER_BUILDER);
        if (counter == null) {
            return NoopCounter.NOOP_COUNTER;
        }
        return counter;
    }

    /**
     * @see #histogram(MetricName)
     */
    public Histogram histogram(String name) {
        return histogram(MetricName.build(name));
    }

    /**
     * Return the {@link Histogram} registered under this name; or create and register
     * a new {@link Histogram} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Histogram}
     */
    public Histogram histogram(MetricName name) {
        return histogram(name, ReservoirType.EXPONENTIALLY_DECAYING);
    }

    /**
     * Create a histogram with given name, and reservoir type
     *
     * @param name the name of the metric
     * @param type the type of reservoir
     * @return a histogram instance
     */
    @Override
    public Histogram histogram(MetricName name, ReservoirType type) {
        Histogram histogram = getOrAdd(name, HISTOGRAM_BUILDER, type);
        if (histogram == null) {
            return NoopHistogram.NOOP_HISTOGRAM;
        }
        return histogram;
    }

    /**
     * @see #meter(MetricName)
     */
    public Metered meter(String name) {
        return meter(MetricName.build(name));
    }

    /**
     * Return the {@link Metered} registered under this name; or create and register
     * a new {@link Metered} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Metered}
     */
    public Metered meter(MetricName name) {
        Metered meter = getOrAdd(name, METER_BUILDER);
        if (meter == null) {
            return NoopMeter.NOOP_METER;
        }
        return meter;
    }

    /**
     * @see #timer(MetricName)
     */
    public Timer timer(String name) {
        return timer(MetricName.build(name));
    }

    /**
     * Return the {@link Timer} registered under this name; or create and register
     * a new {@link Timer} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Timer}
     */
    public Timer timer(MetricName name) {
        return timer(name, ReservoirType.EXPONENTIALLY_DECAYING);
    }

    /**
     * Create a timer with given name, and reservoir type
     *
     * @param name the name of the metric
     * @param type the type of reservoir
     * @return a timer instance
     */
    @Override
    public Timer timer(MetricName name, ReservoirType type) {
        Timer timer = getOrAdd(name, TIMER_BUILDER, type);
        if (timer == null) {
            return NoopTimer.NOOP_TIMER;
        }
        return timer;
    }

    /**
     * Return the {@link Compass} registered under this name; or create and register
     * a new {@link Timer} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Compass}
     */
    @Override
    public Compass compass(MetricName name) {
        return compass(name, ReservoirType.EXPONENTIALLY_DECAYING);
    }

    /**
     * Create a compass with given name, and reservoir type
     *
     * @param name the name of the metric
     * @param type the type of reservoir
     * @return a compass instance
     */
    @Override
    public Compass compass(MetricName name, ReservoirType type) {
        Compass compass = getOrAdd(name, COMPASS_BUILDER, type);
        if (compass == null) {
            return NoopCompass.NOOP_COMPASS;
        }
        return compass;
    }

    /**
     * @see #compass(MetricName)
     */
    @Override
    public Compass compass(String name) {
        return compass(MetricName.build(name));
    }

    @Override
    public FastCompass fastCompass(MetricName name) {
        FastCompass compass = getOrAdd(name, FAST_COMPASS_BUILDER);
        if (compass == null) {
            return NoopFastCompass.NOOP_FAST_COMPASS;
        }
        return compass;
    }

    @Override
    public ClusterHistogram clusterHistogram(MetricName name, long[] buckets) {
        ClusterHistogram clusterHistogram = getOrAddClusterHistogram(name, CLUSTER_HISTOGRAM_BUILDER, buckets);
        if (clusterHistogram == null) {
            return NoopClusterHistogram.NOOP_CLUSTER_HISTOGRAM;
        }
        return clusterHistogram;
    }

    /**
     * Removes the metric with the given name.
     *
     * @param name the name of the metric
     * @return whether or not the metric was removed
     */
    public boolean remove(MetricName name) {
        final Meter metric = metrics.remove(name);
        if (metric != null) {
            onMetricRemoved(name, metric);
            return true;
        }
        return false;
    }

    /**
     * Removes all metrics which match the given predicate.
     *
     * @param predicate a predicate
     */
    public void removeMatching(MetricPredicate predicate) {
        for (Map.Entry<MetricName, Meter> entry : metrics.entrySet()) {
            if (predicate.test(entry.getKey(), entry.getValue())) {
                remove(entry.getKey());
            }
        }
    }

    /**
     * Adds a {@link MetricRegistryListener} to a collection of listeners that will be notified on
     * metric creation.  Listeners will be notified in the order in which they are added.
     * <p/>
     * <b>N.B.:</b> The listener will be notified of all existing metrics when it first registers.
     *
     * @param listener the listener that will be notified
     */
    public void addListener(MetricRegistryListener listener) {
        listeners.add(listener);

        for (Map.Entry<MetricName, Meter> entry : metrics.entrySet()) {
            notifyListenerOfAddedMetric(listener, entry.getValue(), entry.getKey());
        }
    }

    /**
     * Removes a {@link MetricRegistryListener} from this registry's collection of listeners.
     *
     * @param listener the listener that will be removed
     */
    public void removeListener(MetricRegistryListener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns a set of the names of all the metrics in the registry.
     *
     * @return the names of all the metrics
     */
    public Set<MetricName> getNames() {
        return Collections.unmodifiableSortedSet(new TreeSet<MetricName>(metrics.keySet()));
    }

    /**
     * Returns a map of all the gauges in the registry and their names.
     *
     * @return all the gauges in the registry
     */
    public Map<MetricName, Gauge> getGauges() {
        return getGauges(FixedPredicate.TRUE);
    }

    /**
     * Returns a map of all the gauges in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the gauges in the registry
     */
    public Map<MetricName, Gauge> getGauges(MetricPredicate filter) {
        return getMetrics(Gauge.class, filter);
    }

    /**
     * Returns a map of all the counters in the registry and their names.
     *
     * @return all the counters in the registry
     */
    public Map<MetricName, Counter> getCounters() {
        return getCounters(FixedPredicate.TRUE);
    }

    /**
     * Returns a map of all the counters in the registry and their names which match the given
     * filter.
     *
     * @param filter the metric filter to match
     * @return all the counters in the registry
     */
    public Map<MetricName, Counter> getCounters(MetricPredicate filter) {
        return getMetrics(Counter.class, filter);
    }

    /**
     * Returns a map of all the histograms in the registry and their names.
     *
     * @return all the histograms in the registry
     */
    public Map<MetricName, Histogram> getHistograms() {
        return getHistograms(FixedPredicate.TRUE);
    }

    /**
     * Returns a map of all the histograms in the registry and their names which match the given
     * filter.
     *
     * @param filter the metric filter to match
     * @return all the histograms in the registry
     */
    public Map<MetricName, Histogram> getHistograms(MetricPredicate filter) {
        return getMetrics(Histogram.class, filter);
    }

    /**
     * Returns a map of all the meters in the registry and their names.
     *
     * @return all the meters in the registry
     */
    public Map<MetricName, Metered> getMeters() {
        return getMeters(FixedPredicate.TRUE);
    }

    /**
     * Returns a map of all the meters in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the meters in the registry
     */
    public Map<MetricName, Metered> getMeters(MetricPredicate filter) {
        return getMetrics(Metered.class, filter);
    }

    /**
     * Returns a map of all the timers in the registry and their names.
     *
     * @return all the timers in the registry
     */
    public Map<MetricName, Timer> getTimers() {
        return getTimers(FixedPredicate.TRUE);
    }

    /**
     * Returns a map of all the timers in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the timers in the registry
     */
    public Map<MetricName, Timer> getTimers(MetricPredicate filter) {
        return getMetrics(Timer.class, filter);
    }

    @Override
    public Map<MetricName, Compass> getCompasses(MetricPredicate filter) {
        return getMetrics(Compass.class, filter);
    }

    @Override
    public Map<MetricName, Compass> getCompasses() {
        return getCompasses(FixedPredicate.TRUE);
    }

    @Override
    public Map<MetricName, FastCompass> getFastCompasses() {
        return getFastCompasses(FixedPredicate.TRUE);
    }

    @Override
    public Map<MetricName, FastCompass> getFastCompasses(MetricPredicate filter) {
        return getMetrics(FastCompass.class, filter);
    }

    @Override
    public Map<MetricName, ClusterHistogram> getClusterHistograms(MetricPredicate filter) {
        return getMetrics(ClusterHistogram.class, filter);
    }

    @Override
    public Map<MetricName, ClusterHistogram> getClusterHistograms() {
        return getMetrics(ClusterHistogram.class, FixedPredicate.TRUE);
    }

    @Override
    public Map<MetricName, Meter> getMetrics(MetricPredicate filter) {
        final TreeMap<MetricName, Meter> filteredMetrics = new TreeMap<MetricName, Meter>();
        filteredMetrics.putAll(getCounters(filter));
        filteredMetrics.putAll(getMeters(filter));
        filteredMetrics.putAll(getHistograms(filter));
        filteredMetrics.putAll(getGauges(filter));
        filteredMetrics.putAll(getTimers(filter));
        filteredMetrics.putAll(getCompasses(filter));
        filteredMetrics.putAll(getFastCompasses(filter));
        filteredMetrics.putAll(getClusterHistograms(filter));
        return Collections.unmodifiableSortedMap(filteredMetrics);
    }

    /**
     * This is an expensive method that will traverse all the metrics, it should be used under a low frequency.
     *
     * @return the last updated time for the entire MetricRegistry
     */

    public long lastUpdateTime() {
        long latest = 0;
        Map<MetricName, Meter> metrics = new HashMap<MetricName, Meter>();
        metrics.putAll(getCounters(FixedPredicate.TRUE));
        metrics.putAll(getMeters(FixedPredicate.TRUE));
        metrics.putAll(getHistograms(FixedPredicate.TRUE));
        metrics.putAll(getGauges(FixedPredicate.TRUE));
        metrics.putAll(getTimers(FixedPredicate.TRUE));
        metrics.putAll(getCompasses(FixedPredicate.TRUE));
        metrics.putAll(getFastCompasses(FixedPredicate.TRUE));
        metrics.putAll(getClusterHistograms(FixedPredicate.TRUE));
        for (Map.Entry<MetricName, Meter> entry : metrics.entrySet()) {
            if (latest < entry.getValue().lastUpdateTime()) {
                latest = entry.getValue().lastUpdateTime();
            }
        }
        return latest;
    }

    @SuppressWarnings("unchecked")
    private <T extends Meter> T getOrAdd(MetricName name, MetricBuilder<T> builder) {
        final Meter metric = metrics.get(name);
        if (metric != null && builder.isInstance(metric)) {
            return (T) metric;
        } else if (metric == null) {
            try {
                // 当已注册的metric数量太多时，返回一个空实现
                if (metrics.size() >= maxMetricCount) {
                    return null;
                }
                builder = builder.newBuilder().interval(config.period(name.getMetricLevel()));
                T newMetric = builder.newMetric(name);
                if (newMetric == null) {
                    return null;
                }
                return register(name, newMetric);
            } catch (IllegalArgumentException e) {
                final Meter added = metrics.get(name);
                if (builder.isInstance(added)) {
                    return (T) added;
                } else {
                    throw e;
                }
            }
        }
        throw new IllegalArgumentException(name + " is already used for a different type of metric");
    }

    @SuppressWarnings("unchecked")
    private <T extends Meter> T getOrAdd(MetricName name, ReservoirTypeBuilder<T> builder, ReservoirType type) {
        final Meter metric = metrics.get(name);
        if (metric != null && builder.isInstance(metric)) {
            return (T) metric;
        } else if (metric == null) {
            try {
                // 当已注册的metric数量太多时，返回一个空实现
                if (metrics.size() >= maxMetricCount) {
                    return null;
                }
                T newMetric = ((ReservoirTypeMetricBuilder<T>) builder.newBuilder().interval(config.period(name.getMetricLevel()))).newMetric(name, type);
                if (newMetric == null) return null;
                return register(name, newMetric);
            } catch (IllegalArgumentException e) {
                final Meter added = metrics.get(name);
                if (builder.isInstance(added)) {
                    return (T) added;
                } else {
                    throw e;
                }
            }
        }
        throw new IllegalArgumentException(name + " is already used for a different type of metric");
    }

    @SuppressWarnings("unchecked")
    private <T extends Meter> T getOrAddClusterHistogram(MetricName name, ClusterHistogramBuilder builder, long[] buckets) {
        final Meter metric = metrics.get(name);
        if (metric != null && builder.isInstance(metric)) {
            return (T) metric;
        } else if (metric == null) {
            try {
                // 当已注册的metric数量太多时，返回一个空实现
                if (metrics.size() >= maxMetricCount) {
                    return null;
                }
                ClusterHistogram newMetric = ((ClusterHistogramBuilder) builder.newBuilder().interval(config.period(name.getMetricLevel()))).newMetric(name, buckets);
                if (newMetric == null) {
                    return null;
                }
                return register(name, (T) newMetric);
            } catch (IllegalArgumentException e) {
                final Meter added = metrics.get(name);
                if (builder.isInstance(added)) {
                    return (T) added;
                } else {
                    throw e;
                }
            }
        }
        throw new IllegalArgumentException(name + " is already used for a different type of metric");
    }

    private <T extends Meter> SortedMap<MetricName, T> getMetrics(Class<T> klass, MetricPredicate predicate) {
        final TreeMap<MetricName, T> timers = new TreeMap<MetricName, T>();
        for (Map.Entry<MetricName, Meter> entry : metrics.entrySet()) {
            if (klass.isInstance(entry.getValue()) && predicate.test(entry.getKey(), entry.getValue())) {
                timers.put(entry.getKey(), (T) entry.getValue());
            } else if (entry.getValue() instanceof DynamicMetricSet) {
                for (Map.Entry<MetricName, Meter> dynamicEntry : ((DynamicMetricSet) entry.getValue()).getDynamicMetrics().entrySet()) {
                    if (klass.isInstance(dynamicEntry.getValue()) &&
                            predicate.test(dynamicEntry.getKey(), dynamicEntry.getValue())) {
                        timers.put(dynamicEntry.getKey(), (T) dynamicEntry.getValue());
                    }
                }
            }
        }
        return Collections.unmodifiableSortedMap(timers);
    }

    private void onMetricAdded(MetricName name, Meter metric) {
        for (MetricRegistryListener listener : listeners) {
            notifyListenerOfAddedMetric(listener, metric, name);
        }
    }

    private void notifyListenerOfAddedMetric(MetricRegistryListener listener, Meter metric, MetricName name) {
        if (metric instanceof Gauge) {
            listener.onGaugeAdded(name, (Gauge<?>) metric);
        } else if (metric instanceof Counter) {
            listener.onCounterAdded(name, (Counter) metric);
        } else if (metric instanceof Histogram) {
            listener.onHistogramAdded(name, (Histogram) metric);
        } else if (metric instanceof Metered) {
            listener.onMeterAdded(name, (Metered) metric);
        } else if (metric instanceof Timer) {
            listener.onTimerAdded(name, (Timer) metric);
        } else if (metric instanceof Compass) {
            listener.onCompassAdded(name, (Compass) metric);
        } else if (metric instanceof FastCompass) {
            listener.onFastCompassAdded(name, (FastCompass) metric);
        } else {
            throw new IllegalArgumentException("Unknown metric type: " + metric.getClass());
        }
    }

    private void onMetricRemoved(MetricName name, Meter metric) {
        for (MetricRegistryListener listener : listeners) {
            notifyListenerOfRemovedMetric(name, metric, listener);
        }
    }

    private void notifyListenerOfRemovedMetric(MetricName name, Meter metric, MetricRegistryListener listener) {
        if (metric instanceof Gauge) {
            listener.onGaugeRemoved(name);
        } else if (metric instanceof Counter) {
            listener.onCounterRemoved(name);
        } else if (metric instanceof Histogram) {
            listener.onHistogramRemoved(name);
        } else if (metric instanceof Metered) {
            listener.onMeterRemoved(name);
        } else if (metric instanceof Timer) {
            listener.onTimerRemoved(name);
        } else if (metric instanceof Compass) {
            listener.onCompassRemoved(name);
        } else if (metric instanceof FastCompass) {
            listener.onFastCompassRemoved(name);
        } else {
            throw new IllegalArgumentException("Unknown metric type: " + metric.getClass());
        }
    }

    private void registerAll(MetricName prefix, MetricSet metrics) throws IllegalArgumentException {
        if (prefix == null)
            prefix = MetricName.EMPTY;

        for (Map.Entry<MetricName, Meter> entry : metrics.getMetrics().entrySet()) {
            if (entry.getValue() instanceof MetricSet && !(entry.getValue() instanceof DynamicMetricSet)) {
                // skip dynamic metric set, the metrics will be collected later on.
                registerAll(MetricName.join(prefix, entry.getKey()), (MetricSet) entry.getValue());
            } else {
                register(MetricName.join(prefix, entry.getKey()), entry.getValue());
            }
        }
    }

    @Override
    public Map<MetricName, Meter> getMetrics() {
        return Collections.unmodifiableMap(metrics);
    }

}
