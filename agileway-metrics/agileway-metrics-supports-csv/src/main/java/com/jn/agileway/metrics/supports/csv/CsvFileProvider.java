package com.jn.agileway.metrics.supports.csv;

import com.jn.agileway.metrics.core.MetricName;

import java.io.File;

/**
 * This interface allows a pluggable implementation of what file names
 * the {@link CsvReporter} will write to.
 *
 * @since 4.1.0
 */
public interface CsvFileProvider {
    File getFile(File directory, MetricName metricName);
}
