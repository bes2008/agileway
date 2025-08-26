package com.jn.agileway.distributed.session.impl;

import com.jn.agileway.distributed.session.*;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.event.EventPublisherAware;
import com.jn.langx.event.local.SimpleEventPublisher;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @since 3.7.0
 */
public class DefaultSessionManager extends AbstractInitializable implements SessionManager, EventPublisherAware {


    private SessionFactory sessionFactory;
    protected SessionRepository repository;
    private long defaultTimeout = TimeUnit.MINUTES.toMillis(30); // units:mills, 30 min
    private EventPublisher<SessionEvent> eventPublisher;
    private String domain = "SESSION";

    public DefaultSessionManager() {
    }

    public Session getSession(SessionContext context) {
        String id = context.getSessionId();
        if (Strings.isNotBlank(id)) {
            return getSession(id);
        }
        return createSession(context);
    }

    private Session createSession(SessionContext context) {
        Session session = sessionFactory.get(context);

        long maxInactiveInterval = session.getMaxInactiveInterval();
        if (maxInactiveInterval <= 0L) {
            session.setMaxInactiveInterval(getDefaultTimeout());
        }

        Date startTime = session.getStartTime();
        Preconditions.checkNotNull(startTime, "the start time is null");

        Date lastAccessTime = session.getLastAccessTime();
        if (lastAccessTime == null) {
            session.setLastAccessTime(new Date());
        }

        String sessionId = session.getId();
        Preconditions.checkNotEmpty(sessionId, "the session id is empty or null");

        repository.add(session);
        if (session instanceof SessionManagerAware) {
            ((SessionManagerAware) session).setSessionManager(this);
        }
        if (eventPublisher != null) {
            eventPublisher.publish(new SessionEvent(this.domain, SessionEvent.SessionEventType.CREATED, session));
        }
        return session;
    }


    @Override
    public Session getSession(String sessionId) throws SessionException {
        if (Strings.isBlank(sessionId)) {
            return null;
        }
        Session session = repository.getById(sessionId);
        if (session != null) {
            if (!session.isExpired()) {
                session.setLastAccessTime(new Date());
                if (session instanceof SessionManagerAware) {
                    ((SessionManagerAware) session).setSessionManager(this);
                }
                // 目的更新访问时间
                repository.update(session);
                return session;
            } else {
                repository.removeById(session.getId());
                if (eventPublisher != null) {
                    eventPublisher.publish(new SessionEvent(this.domain, SessionEvent.SessionEventType.EXPIRE, session));
                }
            }
        }
        return null;
    }

    @Override
    public void invalidate(Session session) {
        if (session != null) {
            repository.removeById(session.getId());
            if (eventPublisher != null) {
                eventPublisher.publish(new SessionEvent(this.domain, SessionEvent.SessionEventType.INVALIDATED, session));
            }
        }
    }

    public SessionRepository getRepository() {
        return repository;
    }

    public void setRepository(SessionRepository repository) {
        this.repository = repository;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setDefaultTimeout(long defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public long getDefaultTimeout() {
        return defaultTimeout;
    }

    @Override
    public EventPublisher getEventPublisher() {
        return eventPublisher;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        this.eventPublisher = publisher;
    }

    public void addSessionListener(SessionListener sessionListener) {
        this.eventPublisher.addEventListener(this.domain, sessionListener);
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        Preconditions.checkNotEmpty(domain);
        this.domain = domain;
    }

    @Override
    protected void doInit() throws InitializationException {
        if (this.eventPublisher == null) {
            setEventPublisher(new SimpleEventPublisher());
        }
        if (this.sessionFactory == null) {
            setSessionFactory(new SimpleSessionFactory());
        }
        if (this.repository == null) {
            setRepository(new LocalSessionRepository());
        }
    }
}
