package com.jn.agileway.metrics.core.meter.impl;

import com.jn.langx.util.concurrent.longaddr.LongAdder;

/**
 * The abstraction of a bucket for collecting statistics
 *
 * @since 4.1.0
 */
class Bucket {

    /**
     * The timestamp of this bucket
     */
    long timestamp = -1L;

    /**
     * The counter for the bucket, can be updated concurrently
     */
    LongAdder count = new LongAdder();
}
