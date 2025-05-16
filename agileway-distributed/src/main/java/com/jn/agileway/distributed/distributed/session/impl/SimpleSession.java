package com.jn.agileway.distributed.distributed.session.impl;

import com.jn.agileway.distributed.distributed.session.SessionManager;
import com.jn.agileway.distributed.distributed.session.SessionManagerAware;
import com.jn.agileway.distributed.distributed.session.Session;
import com.jn.langx.util.collection.AttributableSet;

import java.util.Date;

/**
 * @since 3.7.0
 */
public class SimpleSession extends AttributableSet implements Session, SessionManagerAware {
    private String id;
    private Date startTime;
    private Date lastAccessTime;
    private long maxInactiveInterval;
    private boolean invalid = false;
    private SessionManager sessionManager;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        if (this.startTime == null) {
            this.startTime = startTime;
        }
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    @Override
    public long getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public void setMaxInactiveInterval(long maxIdleTimeInMillis) {
        this.maxInactiveInterval = maxIdleTimeInMillis;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public boolean isExpired() {
        if (!invalid) {
            return getExpireTime().getTime() <= System.currentTimeMillis();
        }
        return invalid;
    }

    @Override
    public Date getExpireTime() {
        return new Date(lastAccessTime.getTime() + maxInactiveInterval);
    }

    @Override
    public boolean isInvalid() {
        return invalid;
    }

    @Override
    public void invalidate() {
        this.invalid = true;
        if (sessionManager != null) {
            sessionManager.invalidate(this);
        }
    }
}
