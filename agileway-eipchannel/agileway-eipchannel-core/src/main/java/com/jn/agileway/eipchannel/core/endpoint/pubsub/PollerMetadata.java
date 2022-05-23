package com.jn.agileway.eipchannel.core.endpoint.pubsub;

import java.util.concurrent.Executor;

public class PollerMetadata {
    public static final int MAX_MESSAGES_UNBOUNDED = -1;
    private volatile long maxMessagesPerPoll = MAX_MESSAGES_UNBOUNDED;

    private volatile long receiveTimeout = 1000;


    private volatile Executor taskExecutor;

    /**
     * Set the maximum number of messages to receive for each poll.
     * A non-positive value indicates that polling should repeat as long
     * as non-null messages are being received and successfully sent.
     *
     * <p>The default is unbounded.
     *
     * @see #MAX_MESSAGES_UNBOUNDED
     */
    public void setMaxMessagesPerPoll(long maxMessagesPerPoll) {
        this.maxMessagesPerPoll = maxMessagesPerPoll;
    }

    public long getMaxMessagesPerPoll() {
        return this.maxMessagesPerPoll;
    }

    public void setReceiveTimeout(long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    public long getReceiveTimeout() {
        return this.receiveTimeout;
    }


    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public Executor getTaskExecutor() {
        return this.taskExecutor;
    }
}
