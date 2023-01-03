package com.jn.agileway.metrics.core.reporter;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.config.MetricsCollectPeriodConfig;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meter.impl.ClusterHistogram;
import com.jn.agileway.metrics.core.meterset.MetricMeterFactory;
import com.jn.agileway.metrics.core.predicate.CompositeMetricPredicate;
import com.jn.agileway.metrics.core.predicate.MetricMeterPredicate;
import com.jn.agileway.metrics.core.predicate.TimeMetricLevelPredicate;
import com.jn.langx.util.concurrent.CommonThreadFactory;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * report factory 里所有的metrics
 *
 * @since 4.1.0
 */
public abstract class ScheduledReporterV2 implements Reporter {

    private static final Logger LOG = Loggers.getLogger(ScheduledReporterV2.class);

    protected final double durationFactor;
    protected final double rateFactor;

    private final MetricMeterFactory factory;
    private final ScheduledExecutorService executor;
    private final String durationUnit;
    private final String rateUnit;

    private long schedulePeriod = 1;
    private TimeUnit scheduleUnit = TimeUnit.SECONDS;

    private TimeMetricLevelPredicate timeMetricLevelPredicate;
    private CompositeMetricPredicate compositeMetricPredicate;
    private ScheduledFuture futureTask;
    /**
     * 控制Report的启动和停止
     */
    private volatile boolean runFlag = true;
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            if (!runFlag) {
                return;
            }

            try {
                timeMetricLevelPredicate.beforeReport();
                report();
            } catch (Throwable ex) {
                LOG.error("Throwable RuntimeException thrown from {}#report. Exception was suppressed.",
                        ScheduledReporterV2.this.getClass().getSimpleName(), ex);
            } finally {
                timeMetricLevelPredicate.afterReport();
            }
        }
    };

    /**
     * Creates a new {@link ScheduledReporterV2} instance.
     *
     * @param factory      the {@link MetricMeterFactory} containing the metrics this
     *                     reporter will report
     * @param name         the reporter's name
     * @param predicate    the predicate for which metrics to report
     * @param rateUnit     a unit of time
     * @param durationUnit a unit of time
     */
    protected ScheduledReporterV2(MetricMeterFactory factory,
                                  String name,
                                  MetricMeterPredicate predicate,
                                  MetricsCollectPeriodConfig metricsReportPeriodConfig,
                                  TimeUnit rateUnit,
                                  TimeUnit durationUnit) {
        this(factory, predicate, new TimeMetricLevelPredicate(metricsReportPeriodConfig), rateUnit, durationUnit,
                Executors.newSingleThreadScheduledExecutor(new CommonThreadFactory(name, true)));
    }

    /**
     * Creates a new {@link ScheduledReporterV2} instance.
     *
     * @param factory      the {@link MetricMeterFactory} containing the metrics this
     *                     reporter will report
     * @param name         the reporter's name
     * @param predicate    the predicate for which metrics to report
     * @param rateUnit     a unit of time
     * @param durationUnit a unit of time
     */
    protected ScheduledReporterV2(MetricMeterFactory factory,
                                  String name,
                                  MetricMeterPredicate predicate,
                                  TimeMetricLevelPredicate timeMetricLevelFilter,
                                  TimeUnit rateUnit,
                                  TimeUnit durationUnit) {
        this(factory, predicate, timeMetricLevelFilter, rateUnit, durationUnit,
                Executors.newSingleThreadScheduledExecutor(new CommonThreadFactory(name, true)));
    }

    /**
     * Creates a new {@link ScheduledReporterV2} instance.
     *
     * @param factory   the {@link MetricMeterFactory} containing the metrics this
     *                  reporter will report
     * @param predicate the predicate for which metrics to report
     * @param executor  the executor to use while scheduling reporting of metrics.
     */
    protected ScheduledReporterV2(MetricMeterFactory factory,
                                  MetricMeterPredicate predicate,
                                  TimeMetricLevelPredicate timeMetricLevelFilter,
                                  TimeUnit rateUnit,
                                  TimeUnit durationUnit,
                                  ScheduledExecutorService executor) {
        this.factory = factory;
        this.executor = executor;
        this.rateFactor = rateUnit.toSeconds(1);
        this.rateUnit = calculateRateUnit(rateUnit);
        this.durationFactor = 1.0 / durationUnit.toNanos(1);
        this.durationUnit = durationUnit.toString().toLowerCase(Locale.US);
        this.timeMetricLevelPredicate = timeMetricLevelFilter;
        this.compositeMetricPredicate = new CompositeMetricPredicate(timeMetricLevelFilter, predicate);
    }

    /**
     * 暂停report任务
     */
    public void suspension() {
        runFlag = false;
    }

    /**
     * 恢复report任务
     */
    public void resumption() {
        runFlag = true;
    }

    /**
     * Starts the reporter polling at the given period.
     *
     * @param period the amount of time between polls
     * @param unit   the unit for {@code period}
     */
    public void start(long period, TimeUnit unit) {
        this.schedulePeriod = period;
        this.scheduleUnit = unit;
        futureTask = executor.scheduleWithFixedDelay(task, schedulePeriod, schedulePeriod, scheduleUnit);
    }

    /**
     * Stops the reporter and shuts down its thread of execution.
     * <p>
     * Uses the shutdown pattern from:
     * http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html
     */
    public void stop() {
        executor.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(schedulePeriod * 2, scheduleUnit)) {
                executor.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(schedulePeriod * 2, scheduleUnit)) {
                    LOG.warn(getClass().getSimpleName() + ": ScheduledExecutorService did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public void reschedule(long period, TimeUnit unit) {
        // the parameter false mean no interrupt to the running task.
        if (futureTask.cancel(false)) {
            this.schedulePeriod = period;
            this.scheduleUnit = unit;
            futureTask = executor.scheduleWithFixedDelay(task, period, period, unit);
        }
    }

    /**
     * Stops the reporter and shuts down its thread of execution.
     */
    @Override
    public void close() {
        stop();
    }

    /**
     * Report the current values of all metrics in the factory.
     */
    public void report() {
        synchronized (this) {

            Map<Class<? extends Meter>, Map<Metric, ? extends Meter>> categoryMetrics = factory
                    .getAllCategoryMetrics(compositeMetricPredicate);

            report((Map<Metric, Gauge>) categoryMetrics.get(Gauge.class),
                    (Map<Metric, Counter>) categoryMetrics.get(Counter.class),
                    (Map<Metric, Histogram>) categoryMetrics.get(Histogram.class),
                    (Map<Metric, Metered>) categoryMetrics.get(Metered.class),
                    (Map<Metric, Timer>) categoryMetrics.get(Timer.class),
                    (Map<Metric, Compass>) categoryMetrics.get(Compass.class),
                    (Map<Metric, FastCompass>) categoryMetrics.get(FastCompass.class),
                    (Map<Metric, ClusterHistogram>) categoryMetrics.get(ClusterHistogram.class));
        }
    }

    /**
     * Called periodically by the polling thread. Subclasses should report all the given metrics.
     */
    public abstract void report(Map<Metric, Gauge> gauges,
                                Map<Metric, Counter> counters,
                                Map<Metric, Histogram> histograms,
                                Map<Metric, Metered> meters,
                                Map<Metric, Timer> timers,
                                Map<Metric, Compass> compasses,
                                Map<Metric, FastCompass> fastCompasses,
                                Map<Metric, ClusterHistogram> clusterHistograms);

    protected String getRateUnit() {
        return rateUnit;
    }

    protected String getDurationUnit() {
        return durationUnit;
    }

    protected double convertDuration(double duration) {
        return duration * durationFactor;
    }

    protected double convertRate(double rate) {
        return rate * rateFactor;
    }

    private String calculateRateUnit(TimeUnit unit) {
        final String s = unit.toString().toLowerCase(Locale.US);
        return s.substring(0, s.length() - 1);
    }

}