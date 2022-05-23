package com.jn.agileway.eipchannel.core.endpoint.pubsub;

import com.jn.agileway.eipchannel.core.message.MessagingException;
import com.jn.langx.exception.ErrorHandler;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.concurrent.Global;
import com.jn.langx.util.timing.scheduling.Trigger;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;

/**
 * 自动化不断拉取的 consumer，拉取之后可以交给 handler来处理
 */
public class AbstractPollingConsumer extends DefaultMessageConsumer {
    /**
     * 由该 taskExecutor 去执行拉取 并执行 拉取之后的动作
     */
    private volatile Executor pollAndConsumeExecutor;
    private Trigger trigger;
    private ErrorHandler errorHandler;

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    /**
     * 每次拉取的超时时间，单位 mills
     */
    private long timeout;

    public Executor getPollAndConsumeExecutor() {
        return pollAndConsumeExecutor;
    }

    public long getTimeout() {
        return timeout;
    }


    /**
     * just for shutdown
     */
    private volatile ScheduledFuture<?> runningTask;

    private volatile Runnable poller;

    public AbstractPollingConsumer() {
    }

    public void setPollAndConsumeExecutor(Executor pollAndConsumeExecutor) {
        this.pollAndConsumeExecutor = pollAndConsumeExecutor;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public Object poll(long timeout) {
        return super.poll(timeout);
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
        super.doStart();
        this.runningTask = Global.scheduleTask(poller, trigger, errorHandler);
    }

    @Override
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
