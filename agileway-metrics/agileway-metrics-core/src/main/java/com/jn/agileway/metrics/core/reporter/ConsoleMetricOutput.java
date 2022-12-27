package com.jn.agileway.metrics.core.reporter;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meterset.MetricMeterRegistry;
import com.jn.agileway.metrics.core.predicate.FixedPredicate;
import com.jn.agileway.metrics.core.predicate.MetricMeterPredicate;
import com.jn.agileway.metrics.core.snapshot.Snapshot;
import com.jn.langx.util.timing.clock.Clock;
import com.jn.langx.util.timing.clock.Clocks;

import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * A reporter which outputs measurements to a {@link PrintStream}, like {@code System.out}.
 *
 * @since 4.1.0
 */
public class ConsoleMetricOutput extends BaseMetricOutput {
    private static final int CONSOLE_WIDTH = 80;
    private final PrintStream output;
    private final Locale locale;
    private final Clock clock;
    private final DateFormat dateFormat;


    private ConsoleMetricOutput(PrintStream output,
                                Locale locale,
                                Clock clock,
                                TimeZone timeZone,
                                TimeUnit rateUnit,
                                TimeUnit durationUnit,
                                MetricMeterPredicate predicate) {
        super();

        this.output = output;
        this.locale = locale;
        clock = clock == null ? Clocks.defaultClock() : clock;
        this.clock = clock;
        this.dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
        dateFormat.setTimeZone(timeZone);

        setDurationUnit(durationUnit);
        setRateUnit(rateUnit);
        setPredicate(predicate);
    }


    @Override
    public void write(MetricMeterRegistry registry, MetricMeterPredicate predicate) {
        final String dateTime = dateFormat.format(new Date(clock.getTime()));
        printWithBanner(dateTime, '=');
        output.println();
        super.write(registry, predicate);
        output.println();
        output.flush();
    }

    @Override
    protected void writeGauges(Map<Metric, Gauge> metrics) {
        if (!metrics.isEmpty()) {
            printWithBanner("-- Gauges", '-');
            for (Map.Entry<Metric, Gauge> entry : metrics.entrySet()) {
                output.println(entry.getKey());
                printGauge(entry);
            }
            output.println();
        }

    }

    @Override
    protected void writeCounters(Map<Metric, Counter> metrics) {

        if (!metrics.isEmpty()) {
            printWithBanner("-- Counters", '-');
            for (Map.Entry<Metric, Counter> entry : metrics.entrySet()) {
                output.println(entry.getKey());
                printCounter(entry);
            }
            output.println();
        }
    }

    @Override
    protected void writeCompasses(Map<Metric, Compass> metrics) {
        super.writeCompasses(metrics);
    }

    @Override
    protected void writeFastCompasses(Map<Metric, FastCompass> metrics) {
        super.writeFastCompasses(metrics);
    }

    @Override
    protected void writeHistograms(Map<Metric, Histogram> metrics) {
        if (!metrics.isEmpty()) {
            printWithBanner("-- Histograms", '-');
            for (Map.Entry<Metric, Histogram> entry : metrics.entrySet()) {
                output.println(entry.getKey());
                printHistogram(entry.getValue());
            }
            output.println();
        }
    }

    @Override
    protected void writeTimers(Map<Metric, Timer> metrics) {
        if (!metrics.isEmpty()) {
            printWithBanner("-- Timers", '-');
            for (Map.Entry<Metric, Timer> entry : metrics.entrySet()) {
                output.println(entry.getKey());
                printTimer(entry.getValue());
            }
            output.println();
        }
    }

    @Override
    protected void writeMetereds(Map<Metric, Metered> metrics) {
        if (!metrics.isEmpty()) {
            printWithBanner("-- Meters", '-');
            for (Map.Entry<Metric, Metered> entry : metrics.entrySet()) {
                output.println(entry.getKey());
                printMeter(entry.getValue());
            }
            output.println();
        }
    }


    private void printMeter(Metered meter) {
        output.printf(locale, "             count = %d%n", meter.getCount());
        output.printf(locale, "         mean rate = %2.2f events/%s%n", convertRate(meter.getMeanRate()), getRateUnit());
        output.printf(locale, "     1-minute rate = %2.2f events/%s%n", convertRate(meter.getM1Rate()), getRateUnit());
        output.printf(locale, "     5-minute rate = %2.2f events/%s%n", convertRate(meter.getM5Rate()), getRateUnit());
        output.printf(locale, "    15-minute rate = %2.2f events/%s%n", convertRate(meter.getM15Rate()), getRateUnit());
    }

    private void printCounter(Map.Entry<Metric, Counter> entry) {
        output.printf(locale, "             count = %d%n", entry.getValue().getCount());
    }

    private void printGauge(Map.Entry<Metric, Gauge> entry) {
        output.printf(locale, "             value = %s%n", entry.getValue().getValue());
    }

    private void printHistogram(Histogram histogram) {
        output.printf(locale, "             count = %d%n", histogram.getCount());
        Snapshot snapshot = histogram.getSnapshot();
        output.printf(locale, "               min = %d%n", snapshot.getMin());
        output.printf(locale, "               max = %d%n", snapshot.getMax());
        output.printf(locale, "              mean = %2.2f%n", snapshot.getMean());
        output.printf(locale, "            stddev = %2.2f%n", snapshot.getStdDev());
        output.printf(locale, "            median = %2.2f%n", snapshot.getMedian());
        output.printf(locale, "              75%% <= %2.2f%n", snapshot.get75thPercentile());
        output.printf(locale, "              95%% <= %2.2f%n", snapshot.get95thPercentile());
        output.printf(locale, "              98%% <= %2.2f%n", snapshot.get98thPercentile());
        output.printf(locale, "              99%% <= %2.2f%n", snapshot.get99thPercentile());
        output.printf(locale, "            99.9%% <= %2.2f%n", snapshot.get999thPercentile());
    }

    private void printTimer(Timer timer) {
        final Snapshot snapshot = timer.getSnapshot();
        output.printf(locale, "             count = %d%n", timer.getCount());
        output.printf(locale, "         mean rate = %2.2f calls/%s%n", convertRate(timer.getMeanRate()), getRateUnit());
        output.printf(locale, "     1-minute rate = %2.2f calls/%s%n", convertRate(timer.getM1Rate()), getRateUnit());
        output.printf(locale, "     5-minute rate = %2.2f calls/%s%n", convertRate(timer.getM5Rate()), getRateUnit());
        output.printf(locale, "    15-minute rate = %2.2f calls/%s%n", convertRate(timer.getM15Rate()), getRateUnit());

        output.printf(locale, "               min = %2.2f %s%n", convertDuration(snapshot.getMin()), getDurationUnit());
        output.printf(locale, "               max = %2.2f %s%n", convertDuration(snapshot.getMax()), getDurationUnit());
        output.printf(locale, "              mean = %2.2f %s%n", convertDuration(snapshot.getMean()), getDurationUnit());
        output.printf(locale, "            stddev = %2.2f %s%n", convertDuration(snapshot.getStdDev()), getDurationUnit());
        output.printf(locale, "            median = %2.2f %s%n", convertDuration(snapshot.getMedian()), getDurationUnit());
        output.printf(locale, "              75%% <= %2.2f %s%n", convertDuration(snapshot.get75thPercentile()), getDurationUnit());
        output.printf(locale, "              95%% <= %2.2f %s%n", convertDuration(snapshot.get95thPercentile()), getDurationUnit());
        output.printf(locale, "              98%% <= %2.2f %s%n", convertDuration(snapshot.get98thPercentile()), getDurationUnit());
        output.printf(locale, "              99%% <= %2.2f %s%n", convertDuration(snapshot.get99thPercentile()), getDurationUnit());
        output.printf(locale, "            99.9%% <= %2.2f %s%n", convertDuration(snapshot.get999thPercentile()), getDurationUnit());
    }

    private void printWithBanner(String s, char c) {
        output.print(s);
        output.print(' ');
        for (int i = 0; i < (CONSOLE_WIDTH - s.length() - 1); i++) {
            output.print(c);
        }
        output.println();
    }

    /**
     * A builder for {@link ConsoleMetricOutput} instances. Defaults to using the default locale and
     * time zone, writing to {@code System.out}, converting rates to events/second, converting
     * durations to milliseconds, and not filtering metrics.
     */
    public static class Builder {
        private PrintStream output;
        private Locale locale;
        private Clock clock;
        private TimeZone timeZone;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricMeterPredicate filter;

        private Builder() {
            this.output = System.out;
            this.locale = Locale.getDefault();
            this.clock = Clocks.defaultClock();
            this.timeZone = TimeZone.getDefault();
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = FixedPredicate.TRUE;
        }

        /**
         * Write to the given {@link PrintStream}.
         *
         * @param output a {@link PrintStream} instance.
         * @return {@code this}
         */
        public Builder outputTo(PrintStream output) {
            this.output = output;
            return this;
        }

        /**
         * Format numbers for the given {@link Locale}.
         *
         * @param locale a {@link Locale}
         * @return {@code this}
         */
        public Builder formattedFor(Locale locale) {
            this.locale = locale;
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
         * Use the given {@link TimeZone} for the time.
         *
         * @param timeZone a {@link TimeZone}
         * @return {@code this}
         */
        public Builder formattedFor(TimeZone timeZone) {
            this.timeZone = timeZone;
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
         * Only report metrics which match the given filter.
         *
         * @param filter a {@link MetricMeterPredicate}
         * @return {@code this}
         */
        public Builder filter(MetricMeterPredicate filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Builds a {@link ConsoleMetricOutput} with the given properties.
         *
         * @return a {@link ConsoleMetricOutput}
         */
        public ConsoleMetricOutput build() {
            return new ConsoleMetricOutput(output,
                    locale,
                    clock,
                    timeZone,
                    rateUnit,
                    durationUnit,
                    filter);
        }
    }
}
