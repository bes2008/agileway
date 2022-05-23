package com.jn.agileway.eipchannel.core.endpoint.pubsub;

import com.jn.agileway.eipchannel.core.message.MessagingException;
import com.jn.langx.util.Preconditions;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * 自动化不断拉取的 consumer
 */
public class PollingConsumer extends DefaultMessageConsumer {
    /**
     * 由该 taskExecutor 去执行拉取 并执行 拉取之后的动作
     */
    private volatile Executor pollAndConsumeExecutor;

    /**
     * 每次拉取的超时时间，单位 mills
     */
    private long timeout;
    /**
     * 用于调度 poll task 的执行。 开始执行时，交给 pollTaskExecutor
     */
    private ScheduledExecutorService pollTaskScheduler;

    /**
     * just for shutdown
     */
    private volatile ScheduledFuture<?> runningTask;

    private volatile Runnable poller;

    public PollingConsumer() {
    }

    public void setPollAndConsumeExecutor(Executor pollAndConsumeExecutor) {
        this.pollAndConsumeExecutor = pollAndConsumeExecutor;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    protected void doInit() {
        super.doInit();
        Preconditions.checkNotNull(pollAndConsumeExecutor);
        try {
            this.poller = this.createPoller();
        } catch (Exception e) {
            throw new MessagingException("Failed to create Poller", e);
        }
    }

    private Runnable createPoller() {
        Runnable pollingTask = new Runnable() {
            public void run() {
                poll(timeout);
            }
        };

        return new Poller(pollingTask);
    }


    @Override
    protected void doStart() {
        // 延时出发调用
        this.runningTask = null; // this.getTaskScheduler().schedule(this.poller, this.pollerMetadata.getTrigger());
    }

    @Override // guarded by super#lifecycleLock
    protected void doStop() {
        if (this.runningTask != null) {
            this.runningTask.cancel(true);
        }
        this.runningTask = null;
    }


    /**
     * 执行一次 poll
     */
    private class Poller implements Runnable {

        private final Runnable pollingTask;

        public Poller(Runnable pollingTask) {
            this.pollingTask = pollingTask;
        }

        public void run() {
            pollAndConsumeExecutor.execute(pollingTask);
        }
    }

}
