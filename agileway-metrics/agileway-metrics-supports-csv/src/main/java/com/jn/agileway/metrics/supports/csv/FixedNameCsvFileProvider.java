package com.jn.agileway.metrics.supports.csv;

import com.jn.agileway.metrics.core.Metric;

import java.io.File;

/**
 * This implementation of the {@link CsvFileProvider} will always return the same name
 * for the same metric. This means the CSV file will grow indefinitely.
 *
 * @since 4.1.0
 */
public class FixedNameCsvFileProvider implements CsvFileProvider {
    @Override
    public File getFile(File directory, Metric metricName) {
        return new File(directory, sanitize(metricName) + ".csv");
    }

    private String sanitize(Metric metricName) {
        //Forward slash character is definitely illegal in both Windows and Linux
        //https://msdn.microsoft.com/en-us/library/windows/desktop/aa365247(v=vs.85).aspx
        final String sanitizedName = metricName.getKey().replaceFirst("^/", "").replaceAll("/", ".");
        return sanitizedName;
    }
}
