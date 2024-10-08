package com.jn.agileway.metrics.supports.jdk.jmx;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meterset.MetricMeterRegistry;
import com.jn.agileway.metrics.core.meterset.MetricMeterRegistryListener;
import com.jn.agileway.metrics.core.predicate.FixedPredicate;
import com.jn.agileway.metrics.core.predicate.MetricMeterPredicate;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import javax.management.*;
import java.io.Closeable;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * A reporter which listens for new metrics and exposes them as namespaced MBeans.
 *
 * @since 4.1.0
 */
public class JmxReporter implements Closeable {
    private static final Logger LOGGER = Loggers.getLogger(JmxReporter.class);
    private final MetricMeterRegistry registry;
    private final JmxListener listener;

    private JmxReporter(MBeanServer mBeanServer,
                        String domain,
                        MetricMeterRegistry registry,
                        MetricMeterPredicate predicate,
                        MetricTimeUnits timeUnits,
                        ObjectNameFactory objectNameFactory) {
        this.registry = registry;
        this.listener = new JmxListener(mBeanServer, domain, predicate, timeUnits, objectNameFactory);
    }
    // CHECKSTYLE:ON

    /**
     * Returns a new {@link Builder} for {@link JmxReporter}.
     *
     * @param registry the registry to report
     * @return a {@link Builder} instance for a {@link JmxReporter}
     */
    public static Builder forRegistry(MetricMeterRegistry registry) {
        return new Builder(registry);
    }

    /**
     * Starts the reporter.
     */
    public void start() {
        registry.addListener(listener);
    }
    // CHECKSTYLE:ON

    /**
     * Stops the reporter.
     */
    public void stop() {
        registry.removeListener(listener);
        listener.unregisterAll();
    }

    /**
     * Stops the reporter.
     */
    @Override
    public void close() {
        stop();
    }
    // CHECKSTYLE:ON

    /**
     * Visible for testing
     */
    ObjectNameFactory getObjectNameFactory() {
        return listener.objectNameFactory;
    }

    // CHECKSTYLE:OFF
    @SuppressWarnings("UnusedDeclaration")
    public interface MetricMBean {
        ObjectName objectName();
    }
    // CHECKSTYLE:ON

    // CHECKSTYLE:OFF
    @SuppressWarnings("UnusedDeclaration")
    public interface JmxGaugeMBean extends MetricMBean {
        Object getValue();
    }

    // CHECKSTYLE:OFF
    @SuppressWarnings("UnusedDeclaration")
    public interface JmxCounterMBean extends MetricMBean {
        long getCount();
    }
    //CHECKSTYLE:ON

    // CHECKSTYLE:OFF
    @SuppressWarnings("UnusedDeclaration")
    public interface JmxHistogramMBean extends MetricMBean {
        long getCount();

        long getMin();

        long getMax();

        double getMean();

        double getStdDev();

        double get50thPercentile();

        double get75thPercentile();

        double get95thPercentile();

        double get98thPercentile();

        double get99thPercentile();

        double get999thPercentile();

        long[] values();
    }

    //CHECKSTYLE:OFF
    @SuppressWarnings("UnusedDeclaration")
    public interface JmxMeterMBean extends MetricMBean {
        long getCount();

        double getMeanRate();

        double getOneMinuteRate();

        double getFiveMinuteRate();

        double getFifteenMinuteRate();

        String getRateUnit();
    }
    // CHECKSTYLE:ON

    // CHECKSTYLE:OFF
    @SuppressWarnings("UnusedDeclaration")
    public interface JmxTimerMBean extends JmxMeterMBean {
        double getMin();

        double getMax();

        double getMean();

        double getStdDev();

        double get50thPercentile();

        double get75thPercentile();

        double get95thPercentile();

        double get98thPercentile();

        double get99thPercentile();

        double get999thPercentile();

        long[] values();

        String getDurationUnit();
    }

    /**
     * A builder for {@link JmxReporter} instances. Defaults to using the default MBean server and
     * not filtering metrics.
     */
    public static class Builder {
        private final MetricMeterRegistry registry;
        private MBeanServer mBeanServer;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private ObjectNameFactory objectNameFactory;
        private MetricMeterPredicate filter = FixedPredicate.TRUE;
        private String domain;
        private Map<String, TimeUnit> specificDurationUnits;
        private Map<String, TimeUnit> specificRateUnits;

        private Builder(MetricMeterRegistry registry) {
            this.registry = registry;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.domain = "metrics";
            this.objectNameFactory = new DefaultObjectNameFactory();
            this.specificDurationUnits = Collections.emptyMap();
            this.specificRateUnits = Collections.emptyMap();
        }

        /**
         * Register MBeans with the given {@link MBeanServer}.
         *
         * @param mBeanServer an {@link MBeanServer}
         * @return {@code this}
         */
        public Builder registerWith(MBeanServer mBeanServer) {
            this.mBeanServer = mBeanServer;
            return this;
        }

        /**
         * Convert rates to the given time unit.
         *
         * @param rateUnit a unit of time
         * @return {@code this}
         */
        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        public Builder createsObjectNamesWith(ObjectNameFactory onFactory) {
            if (onFactory == null) {
                throw new IllegalArgumentException("null objectNameFactory");
            }
            this.objectNameFactory = onFactory;
            return this;
        }

        /**
         * Convert durations to the given time unit.
         *
         * @param durationUnit a unit of time
         * @return {@code this}
         */
        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        /**
         * Only report metrics which match the given filter.
         *
         * @param filter a {@link MetricMeterPredicate}
         * @return {@code this}
         */
        public Builder filter(MetricMeterPredicate filter) {
            this.filter = filter;
            return this;
        }

        public Builder inDomain(String domain) {
            this.domain = domain;
            return this;
        }

        /**
         * Use specific {@link TimeUnit}s for the duration of the metrics with these names.
         *
         * @param specificDurationUnits a map of metric names and specific {@link TimeUnit}s
         * @return {@code this}
         */
        public Builder specificDurationUnits(Map<String, TimeUnit> specificDurationUnits) {
            this.specificDurationUnits = Collections.unmodifiableMap(specificDurationUnits);
            return this;
        }


        /**
         * Use specific {@link TimeUnit}s for the rate of the metrics with these names.
         *
         * @param specificRateUnits a map of metric names and specific {@link TimeUnit}s
         * @return {@code this}
         */
        public Builder specificRateUnits(Map<String, TimeUnit> specificRateUnits) {
            this.specificRateUnits = Collections.unmodifiableMap(specificRateUnits);
            return this;
        }

        /**
         * Builds a {@link JmxReporter} with the given properties.
         *
         * @return a {@link JmxReporter}
         */
        public JmxReporter build() {
            final MetricTimeUnits timeUnits = new MetricTimeUnits(rateUnit, durationUnit, specificRateUnits, specificDurationUnits);
            if (mBeanServer == null) {
                mBeanServer = ManagementFactory.getPlatformMBeanServer();
            }
            return new JmxReporter(mBeanServer, domain, registry, filter, timeUnits, objectNameFactory);
        }
    }

    private abstract static class AbstractBean implements MetricMBean {
        private final ObjectName objectName;

        AbstractBean(ObjectName objectName) {
            this.objectName = objectName;
        }

        @Override
        public ObjectName objectName() {
            return objectName;
        }
    }

    private static class JmxGauge extends AbstractBean implements JmxGaugeMBean {
        private final Gauge<?> metric;

        private JmxGauge(Gauge<?> metric, ObjectName objectName) {
            super(objectName);
            this.metric = metric;
        }

        @Override
        public Object getValue() {
            return metric.getValue();
        }
    }

    private static class JmxCounter extends AbstractBean implements JmxCounterMBean {
        private final Counter metric;

        private JmxCounter(Counter metric, ObjectName objectName) {
            super(objectName);
            this.metric = metric;
        }

        @Override
        public long getCount() {
            return metric.getCount();
        }
    }

    private static class JmxHistogram implements JmxHistogramMBean {
        private final ObjectName objectName;
        private final Histogram metric;

        private JmxHistogram(Histogram metric, ObjectName objectName) {
            this.metric = metric;
            this.objectName = objectName;
        }

        @Override
        public ObjectName objectName() {
            return objectName;
        }

        @Override
        public double get50thPercentile() {
            return metric.getSnapshot().getMedian();
        }

        @Override
        public long getCount() {
            return metric.getCount();
        }

        @Override
        public long getMin() {
            return metric.getSnapshot().getMin();
        }

        @Override
        public long getMax() {
            return metric.getSnapshot().getMax();
        }

        @Override
        public double getMean() {
            return metric.getSnapshot().getMean();
        }

        @Override
        public double getStdDev() {
            return metric.getSnapshot().getStdDev();
        }

        @Override
        public double get75thPercentile() {
            return metric.getSnapshot().get75thPercentile();
        }

        @Override
        public double get95thPercentile() {
            return metric.getSnapshot().get95thPercentile();
        }

        @Override
        public double get98thPercentile() {
            return metric.getSnapshot().get98thPercentile();
        }

        @Override
        public double get99thPercentile() {
            return metric.getSnapshot().get99thPercentile();
        }

        @Override
        public double get999thPercentile() {
            return metric.getSnapshot().get999thPercentile();
        }

        @Override
        public long[] values() {
            return metric.getSnapshot().getValues();
        }
    }

    private static class JmxMetered extends AbstractBean implements JmxMeterMBean {
        private final Metered meter;
        private final double rateFactor;
        private final String rateUnit;

        private JmxMetered(Metered metered, ObjectName objectName, TimeUnit rateUnit) {
            super(objectName);
            this.meter = metered;
            this.rateFactor = rateUnit.toSeconds(1);
            this.rateUnit = ("events/" + calculateRateUnit(rateUnit)).intern();
        }

        @Override
        public long getCount() {
            return meter.getCount();
        }

        @Override
        public double getMeanRate() {
            return meter.getMeanRate() * rateFactor;
        }

        @Override
        public double getOneMinuteRate() {
            return meter.getM1Rate() * rateFactor;
        }

        @Override
        public double getFiveMinuteRate() {
            return meter.getM5Rate() * rateFactor;
        }

        @Override
        public double getFifteenMinuteRate() {
            return meter.getM15Rate() * rateFactor;
        }

        @Override
        public String getRateUnit() {
            return rateUnit;
        }


    }

    private static String calculateRateUnit(TimeUnit unit) {
        final String s = unit.toString().toLowerCase(Locale.US);
        return s.substring(0, s.length() - 1);
    }

    static class JmxTimer extends AbstractBean implements JmxTimerMBean {
        private final Timer meter;
        private final double durationFactor;
        private final String durationUnit;
        private final double rateFactor;
        private final String rateUnit;

        private JmxTimer(Timer timer,
                         ObjectName objectName,
                         TimeUnit rateUnit,
                         TimeUnit durationUnit) {
            super(objectName);
            this.meter = timer;
            this.rateFactor = rateUnit.toSeconds(1);
            this.rateUnit = ("events/" + calculateRateUnit(rateUnit)).intern();
            this.durationFactor = 1.0 / durationUnit.toNanos(1);
            this.durationUnit = durationUnit.toString().toLowerCase(Locale.US);
        }

        @Override
        public double getMeanRate() {
            return meter.getMeanRate() * rateFactor;
        }

        @Override
        public double getOneMinuteRate() {
            return meter.getM1Rate() * rateFactor;
        }

        @Override
        public double getFiveMinuteRate() {
            return meter.getM5Rate() * rateFactor;
        }

        @Override
        public double getFifteenMinuteRate() {
            return meter.getM15Rate() * rateFactor;
        }

        @Override
        public String getRateUnit() {
            return rateUnit;
        }

        @Override
        public long getCount() {
            return meter.getCount();
        }

        @Override
        public double get50thPercentile() {
            return meter.getSnapshot().getMedian() * durationFactor;
        }

        @Override
        public double getMin() {
            return meter.getSnapshot().getMin() * durationFactor;
        }

        @Override
        public double getMax() {
            return meter.getSnapshot().getMax() * durationFactor;
        }

        @Override
        public double getMean() {
            return meter.getSnapshot().getMean() * durationFactor;
        }

        @Override
        public double getStdDev() {
            return meter.getSnapshot().getStdDev() * durationFactor;
        }

        @Override
        public double get75thPercentile() {
            return meter.getSnapshot().get75thPercentile() * durationFactor;
        }

        @Override
        public double get95thPercentile() {
            return meter.getSnapshot().get95thPercentile() * durationFactor;
        }

        @Override
        public double get98thPercentile() {
            return meter.getSnapshot().get98thPercentile() * durationFactor;
        }

        @Override
        public double get99thPercentile() {
            return meter.getSnapshot().get99thPercentile() * durationFactor;
        }

        @Override
        public double get999thPercentile() {
            return meter.getSnapshot().get999thPercentile() * durationFactor;
        }

        @Override
        public long[] values() {
            return meter.getSnapshot().getValues();
        }

        @Override
        public String getDurationUnit() {
            return durationUnit;
        }
    }

    private static class JmxListener implements MetricMeterRegistryListener {
        private final String name;
        private final MBeanServer mBeanServer;
        private final MetricMeterPredicate predicate;
        private final MetricTimeUnits timeUnits;
        private final Map<ObjectName, ObjectName> registered;
        private final ObjectNameFactory objectNameFactory;

        private JmxListener(MBeanServer mBeanServer, String name, MetricMeterPredicate predicate, MetricTimeUnits timeUnits, ObjectNameFactory objectNameFactory) {
            this.mBeanServer = mBeanServer;
            this.name = name;
            this.predicate = predicate;
            this.timeUnits = timeUnits;
            this.registered = new ConcurrentHashMap<ObjectName, ObjectName>();
            this.objectNameFactory = objectNameFactory;
        }

        private void registerMBean(Object mBean, ObjectName objectName) throws InstanceAlreadyExistsException, JMException {
            ObjectInstance objectInstance = mBeanServer.registerMBean(mBean, objectName);
            if (objectInstance != null) {
                // the websphere mbeanserver rewrites the objectname to include
                // cell, node & server info
                // make sure we capture the new objectName for unregistration
                registered.put(objectName, objectInstance.getObjectName());
            } else {
                registered.put(objectName, objectName);
            }
        }

        private void unregisterMBean(ObjectName originalObjectName) throws InstanceNotFoundException, MBeanRegistrationException {
            ObjectName storedObjectName = registered.remove(originalObjectName);
            if (storedObjectName != null) {
                mBeanServer.unregisterMBean(storedObjectName);
            } else {
                mBeanServer.unregisterMBean(originalObjectName);
            }
        }

        @Override
        public void onGaugeAdded(Metric name, Gauge<?> gauge) {
            try {
                if (predicate.test(name, gauge)) {
                    final ObjectName objectName = createName("gauges", name);
                    registerMBean(new JmxGauge(gauge, objectName), objectName);
                }
            } catch (InstanceAlreadyExistsException e) {
                LOGGER.debug("Unable to register gauge", e);
            } catch (JMException e) {
                LOGGER.warn("Unable to register gauge", e);
            }
        }

        @Override
        public void onGaugeRemoved(Metric name) {
            try {
                final ObjectName objectName = createName("gauges", name);
                unregisterMBean(objectName);
            } catch (InstanceNotFoundException e) {
                LOGGER.debug("Unable to unregister gauge", e);
            } catch (MBeanRegistrationException e) {
                LOGGER.warn("Unable to unregister gauge", e);
            }
        }

        @Override
        public void onCounterAdded(Metric name, Counter counter) {
            try {
                if (predicate.test(name, counter)) {
                    final ObjectName objectName = createName("counters", name);
                    registerMBean(new JmxCounter(counter, objectName), objectName);
                }
            } catch (InstanceAlreadyExistsException e) {
                LOGGER.debug("Unable to register counter", e);
            } catch (JMException e) {
                LOGGER.warn("Unable to register counter", e);
            }
        }

        @Override
        public void onCounterRemoved(Metric name) {
            try {
                final ObjectName objectName = createName("counters", name);
                unregisterMBean(objectName);
            } catch (InstanceNotFoundException e) {
                LOGGER.debug("Unable to unregister counter", e);
            } catch (MBeanRegistrationException e) {
                LOGGER.warn("Unable to unregister counter", e);
            }
        }

        @Override
        public void onHistogramAdded(Metric name, Histogram histogram) {
            try {
                if (predicate.test(name, histogram)) {
                    final ObjectName objectName = createName("histograms", name);
                    registerMBean(new JmxHistogram(histogram, objectName), objectName);
                }
            } catch (InstanceAlreadyExistsException e) {
                LOGGER.debug("Unable to register histogram", e);
            } catch (JMException e) {
                LOGGER.warn("Unable to register histogram", e);
            }
        }

        @Override
        public void onHistogramRemoved(Metric name) {
            try {
                final ObjectName objectName = createName("histograms", name);
                unregisterMBean(objectName);
            } catch (InstanceNotFoundException e) {
                LOGGER.debug("Unable to unregister histogram", e);
            } catch (MBeanRegistrationException e) {
                LOGGER.warn("Unable to unregister histogram", e);
            }
        }

        @Override
        public void onMeterAdded(Metric name, Metered meter) {
            try {
                if (predicate.test(name, meter)) {
                    final ObjectName objectName = createName("meters", name);
                    registerMBean(new JmxMetered(meter, objectName, timeUnits.rateFor(name.getKey())), objectName);
                }
            } catch (InstanceAlreadyExistsException e) {
                LOGGER.debug("Unable to register meter", e);
            } catch (JMException e) {
                LOGGER.warn("Unable to register meter", e);
            }
        }

        @Override
        public void onMeterRemoved(Metric name) {
            try {
                final ObjectName objectName = createName("meters", name);
                unregisterMBean(objectName);
            } catch (InstanceNotFoundException e) {
                LOGGER.debug("Unable to unregister meter", e);
            } catch (MBeanRegistrationException e) {
                LOGGER.warn("Unable to unregister meter", e);
            }
        }

        @Override
        public void onTimerAdded(Metric name, Timer timer) {
            try {
                if (predicate.test(name, timer)) {
                    final ObjectName objectName = createName("timers", name);
                    registerMBean(new JmxTimer(timer, objectName, timeUnits.rateFor(name.getKey()), timeUnits.durationFor(name.getKey())), objectName);
                }
            } catch (InstanceAlreadyExistsException e) {
                LOGGER.debug("Unable to register timer", e);
            } catch (JMException e) {
                LOGGER.warn("Unable to register timer", e);
            }
        }

        @Override
        public void onTimerRemoved(Metric name) {
            try {
                final ObjectName objectName = createName("timers", name);
                unregisterMBean(objectName);
            } catch (InstanceNotFoundException e) {
                LOGGER.debug("Unable to unregister timer", e);
            } catch (MBeanRegistrationException e) {
                LOGGER.warn("Unable to unregister timer", e);
            }
        }

        @Override
        public void onCompassAdded(Metric name, Compass compass) {
            // TODO
        }

        @Override
        public void onCompassRemoved(Metric name) {
            // TODO
        }

        @Override
        public void onFastCompassAdded(Metric name, FastCompass compass) {
            // TODO
        }

        @Override
        public void onFastCompassRemoved(Metric name) {
            // TODO
        }

        private ObjectName createName(String type, Metric name) {
            return objectNameFactory.createName(type, this.name, name);
        }

        void unregisterAll() {
            for (ObjectName name : registered.keySet()) {
                try {
                    unregisterMBean(name);
                } catch (InstanceNotFoundException e) {
                    LOGGER.debug("Unable to unregister metric", e);
                } catch (MBeanRegistrationException e) {
                    LOGGER.warn("Unable to unregister metric", e);
                }
            }
            registered.clear();
        }
    }

    private static class MetricTimeUnits {
        private final TimeUnit defaultRate;
        private final TimeUnit defaultDuration;
        private final Map<String, TimeUnit> rateOverrides;
        private final Map<String, TimeUnit> durationOverrides;

        MetricTimeUnits(TimeUnit defaultRate,
                        TimeUnit defaultDuration,
                        Map<String, TimeUnit> rateOverrides,
                        Map<String, TimeUnit> durationOverrides) {
            this.defaultRate = defaultRate;
            this.defaultDuration = defaultDuration;
            this.rateOverrides = rateOverrides;
            this.durationOverrides = durationOverrides;
        }

        public TimeUnit durationFor(String name) {
            return durationOverrides.containsKey(name) ? durationOverrides.get(name) : defaultDuration;
        }

        public TimeUnit rateFor(String name) {
            return rateOverrides.containsKey(name) ? rateOverrides.get(name) : defaultRate;
        }
    }

}
