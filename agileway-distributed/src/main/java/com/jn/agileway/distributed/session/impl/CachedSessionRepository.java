package com.jn.agileway.distributed.session.impl;

import com.jn.agileway.distributed.session.SessionRepository;
import com.jn.langx.Delegatable;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.cache.Cache;
import com.jn.agileway.distributed.session.Session;

import java.util.concurrent.TimeUnit;

/**
 * @since 3.7.0
 */
public class CachedSessionRepository implements SessionRepository, Delegatable<SessionRepository> {
    @NonNull
    private Cache<String, Session> cache;
    @NonNull
    private SessionRepository delegate;

    public SessionRepository getDelegate() {
        return delegate;
    }

    public void setDelegate(SessionRepository delegate) {
        this.delegate = delegate;
    }

    public Cache<String, Session> getCache() {
        return cache;
    }

    public void setCache(Cache<String, Session> cache) {
        this.cache = cache;
    }

    @Override
    public Session getById(String id) {
        Session session = cache.get(id);
        if (session == null) {
            session = delegate.getById(id);
        }
        return session;
    }

    @Override
    public void add(Session session) {
        cache.set(session.getId(), session, session.getMaxInactiveInterval(), TimeUnit.MILLISECONDS);
        delegate.add(session);
    }

    @Override
    public void update(Session session) {
        cache.set(session.getId(), session, session.getMaxInactiveInterval(), TimeUnit.MILLISECONDS);
        delegate.update(session);
    }

    @Override
    public void removeById(String id) {
        cache.remove(id);
        delegate.removeById(id);
    }
}
