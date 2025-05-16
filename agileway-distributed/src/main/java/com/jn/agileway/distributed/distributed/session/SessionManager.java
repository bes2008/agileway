package com.jn.agileway.distributed.distributed.session;

/**
 * A SessionManager manages the creation, maintenance, and clean-up of all application
 * {@link Session}s.
 *
 * @since 3.7.0
 */
public interface SessionManager {
    void setDomain(String domain);
    String getDomain();

    void setSessionFactory(SessionFactory sessionFactory);

    SessionFactory getSessionFactory();
    /**
     * 基于context获取session，若存在则获取，若不存在则创建
     */
    Session getSession(SessionContext context);
    Session getSession(String id);

    void invalidate(Session session);
}
