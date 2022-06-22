package com.jn.agileway.eipchannel.core.endpoint.pubsub;

import com.jn.agileway.eipchannel.core.message.MessagingException;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.exception.ErrorHandler;
import com.jn.langx.util.timing.scheduling.ScheduledExecutors;
import com.jn.langx.util.timing.scheduling.Trigger;
import com.jn.langx.util.timing.timer.Timeout;
import com.jn.langx.util.timing.timer.Timer;
import com.jn.langx.util.timing.timer.WheelTimers;

/**
 * 自动化不断拉取的 consumer，拉取之后可以交给 handler来处理
 */
public abstract class AbstractPollingConsumer extends DefaultMessageConsumer {
    @Nullable
    private Trigger trigger;
    @Nullable
    private ErrorHandler errorHandler;


    /**
     * 每次拉取的超时时间，单位 mills
     */
    private long timeout = -1;


    public long getTimeout() {
        return timeout;
    }


    /**
     * just for shutdown
     */
    private volatile Timeout runningTask;
    private Timer timer;
    private volatile Runnable poller;

    public void setTimer(Timer timer) {
        this.timer = timer;
    }


    @Override
    public Object poll(long timeout) {
        return super.poll(timeout);
    }

    @Override
    protected void doInit() {
        super.doInit();
        try {
            this.poller = this.createPoller();

            if (timer == null) {
                this.timer = WheelTimers.newHashedWheelTimer();
            }
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

        return pollingTask;
    }


    @Override
    protected void doStart() {
        // 延时出发调用
        super.doStart();
        this.runningTask = ScheduledExecutors.timeoutTask(timer, poller, trigger, errorHandler);
    }

    @Override
    protected void doStop() {
        if (this.runningTask != null) {
            this.runningTask.cancel();
        }
        this.runningTask = null;
        super.doStop();
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

}
