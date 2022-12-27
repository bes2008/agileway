package com.jn.agileway.metrics.supports.csv;

import com.jn.agileway.metrics.core.*;
import com.jn.agileway.metrics.core.predicate.FixedPredicate;
import com.jn.agileway.metrics.core.predicate.MetricMeterPredicate;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meterset.MetricMeterRegistry;
import com.jn.agileway.metrics.core.reporter.ScheduledReporter;
import com.jn.agileway.metrics.core.snapshot.Snapshot;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.timing.clock.Clock;
import com.jn.langx.util.timing.clock.Clocks;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A reporter which creates a comma-separated values file of the measurements for each metric.
 *
 * @since 4.1.0
 */
public class CsvReporter extends ScheduledReporter {
    private static final Logger LOGGER = Loggers.getLogger(CsvReporter.class);
    private static final Charset UTF_8 = Charsets.UTF_8;
    private final File directory;
    private final Locale locale;
    private final Clock clock;
    private final CsvFileProvider csvFileProvider;
    private CsvReporter(MetricMeterRegistry registry,
                        File directory,
                        Locale locale,
                        TimeUnit rateUnit,
                        TimeUnit durationUnit,
                        Clock clock,
                        MetricMeterPredicate filter,
                        CsvFileProvider csvFileProvider) {
        super(registry, "csv-reporter", filter, rateUnit, durationUnit);
        this.directory = directory;
        this.locale = locale;
        this.clock = clock;
        this.csvFileProvider = csvFileProvider;
    }

    /**
     * Returns a new {@link Builder} for {@link CsvReporter}.
     *
     * @param registry the registry to report
     * @return a {@link Builder} instance for a {@link CsvReporter}
     */
    public static Builder forRegistry(MetricMeterRegistry registry) {
        return new Builder(registry);
    }

    @Override
    public void report(Map<Metric, Gauge> gauges,
                       Map<Metric, Counter> counters,
                       Map<Metric, Histogram> histograms,
                       Map<Metric, Metered> meters,
                       Map<Metric, Timer> timers) {
        final long timestamp = TimeUnit.MILLISECONDS.toSeconds(clock.getTime());

        for (Map.Entry<Metric, Gauge> entry : gauges.entrySet()) {
            reportGauge(timestamp, entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Metric, Counter> entry : counters.entrySet()) {
            reportCounter(timestamp, entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Metric, Histogram> entry : histograms.entrySet()) {
            reportHistogram(timestamp, entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Metric, Metered> entry : meters.entrySet()) {
            reportMeter(timestamp, entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Metric, Timer> entry : timers.entrySet()) {
            reportTimer(timestamp, entry.getKey(), entry.getValue());
        }
    }

    private void reportTimer(long timestamp, Metric name, Timer timer) {
        final Snapshot snapshot = timer.getSnapshot();

        report(timestamp,
                name,
                "count,max,mean,min,stddev,p50,p75,p95,p98,p99,p999,mean_rate,m1_rate,m5_rate,m15_rate,rate_unit,duration_unit",
                "%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,calls/%s,%s",
                timer.getCount(),
                convertDuration(snapshot.getMax()),
                convertDuration(snapshot.getMean()),
                convertDuration(snapshot.getMin()),
                convertDuration(snapshot.getStdDev()),
                convertDuration(snapshot.getMedian()),
                convertDuration(snapshot.get75thPercentile()),
                convertDuration(snapshot.get95thPercentile()),
                convertDuration(snapshot.get98thPercentile()),
                convertDuration(snapshot.get99thPercentile()),
                convertDuration(snapshot.get999thPercentile()),
                convertRate(timer.getMeanRate()),
                convertRate(timer.getM1Rate()),
                convertRate(timer.getM5Rate()),
                convertRate(timer.getM15Rate()),
                getRateUnit(),
                getDurationUnit());
    }

    private void reportMeter(long timestamp, Metric name, Metered meter) {
        report(timestamp,
                name,
                "count,mean_rate,m1_rate,m5_rate,m15_rate,rate_unit",
                "%d,%f,%f,%f,%f,events/%s",
                meter.getCount(),
                convertRate(meter.getMeanRate()),
                convertRate(meter.getM1Rate()),
                convertRate(meter.getM5Rate()),
                convertRate(meter.getM15Rate()),
                getRateUnit());
    }

    private void reportHistogram(long timestamp, Metric name, Histogram histogram) {
        final Snapshot snapshot = histogram.getSnapshot();

        report(timestamp,
                name,
                "count,max,mean,min,stddev,p50,p75,p95,p98,p99,p999",
                "%d,%d,%f,%d,%f,%f,%f,%f,%f,%f,%f",
                histogram.getCount(),
                snapshot.getMax(),
                snapshot.getMean(),
                snapshot.getMin(),
                snapshot.getStdDev(),
                snapshot.getMedian(),
                snapshot.get75thPercentile(),
                snapshot.get95thPercentile(),
                snapshot.get98thPercentile(),
                snapshot.get99thPercentile(),
                snapshot.get999thPercentile());
    }

    private void reportCounter(long timestamp, Metric name, Counter counter) {
        report(timestamp, name, "count", "%d", counter.getCount());
    }

    private void reportGauge(long timestamp, Metric name, Gauge gauge) {
        report(timestamp, name, "value", "%s", gauge.getValue());
    }

    private void report(long timestamp, Metric name, String header, String line, Object... values) {
        try {
            final File file = csvFileProvider.getFile(directory, name);
            final boolean fileAlreadyExists = file.exists();
            if (fileAlreadyExists || file.createNewFile()) {
                final PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), UTF_8));
                try {
                    if (!fileAlreadyExists) {
                        out.println("t," + header);
                    }
                    out.printf(locale, String.format(locale, "%d,%s%n", timestamp, line), values);
                } finally {
                    out.close();
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Error writing to {}", name, e);
        }
    }

    /**
     * A builder for {@link CsvReporter} instances. Defaults to using the default locale, converting
     * rates to events/second, converting durations to milliseconds, and not filtering metrics.
     */
    public static class Builder {
        private final MetricMeterRegistry registry;
        private Locale locale;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private Clock clock;
        private MetricMeterPredicate filter;
        private CsvFileProvider csvFileProvider;

        private Builder(MetricMeterRegistry registry) {
            this.registry = registry;
            this.locale = Locale.getDefault();
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.clock = Clocks.defaultClock();
            this.filter = FixedPredicate.TRUE;
            this.csvFileProvider = new FixedNameCsvFileProvider();
        }

        /**
         * Format numbers for the given {@link Locale}.
         *
         * @param locale a {@link Locale}
         * @return {@code this}
         */
        public Builder formatFor(Locale locale) {
            this.locale = locale;
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
         * Use the given {@link Clock} instance for the time.
         *
         * @param clock a {@link Clock} instance
         * @return {@code this}
         */
        public Builder withClock(Clock clock) {
            this.clock = clock;
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

        public Builder withCsvFileProvider(CsvFileProvider csvFileProvider) {
            this.csvFileProvider = csvFileProvider;
            return this;
        }

        /**
         * Builds a {@link CsvReporter} with the given properties, writing {@code .csv} files to the
         * given directory.
         *
         * @param directory the directory in which the {@code .csv} files will be created
         * @return a {@link CsvReporter}
         */
        public CsvReporter build(File directory) {
            return new CsvReporter(registry,
                    directory,
                    locale,
                    rateUnit,
                    durationUnit,
                    clock,
                    filter,
                    csvFileProvider);
        }
    }
}
