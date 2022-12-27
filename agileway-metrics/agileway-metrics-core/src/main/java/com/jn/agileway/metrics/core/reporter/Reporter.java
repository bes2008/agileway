package com.jn.agileway.metrics.core.reporter;

import java.io.Closeable;

/**
 * @since 4.1.0
 */
public interface Reporter extends Closeable {
    void report();
}
