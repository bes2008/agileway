package com.jn.agileway.distributed.distributed.session;


import com.jn.langx.util.collection.Attributable;

import java.util.Date;

/**
 * A {@code Session} is a stateful data context associated with a single Subject (user, daemon process,
 * etc) who interacts with a software system over a period of time.
 * <p/>
 * A {@code Session} is intended to be managed by the business tier and accessible via other
 * tiers without being tied to any given client technology.  This is a <em>great</em> benefit to Java
 * systems, since until now, the only viable session mechanisms were the
 * {@code javax.servlet.http.HttpSession} or Stateful Session EJB's, which many times
 * unnecessarily coupled applications to web or ejb technologies.
 *
 * @since 3.7.0
 */
public interface Session extends Attributable {
    String getId();

    Date getStartTime();

    Date getLastAccessTime();

    void setLastAccessTime(Date date);

    long getMaxInactiveInterval();

    void setMaxInactiveInterval(long maxIdleTimeInMillis);

    boolean isExpired();

    Date getExpireTime();

    boolean isInvalid();

    void invalidate();

    @Override
    void setAttribute(String name, Object value);

    @Override
    Object getAttribute(String name);

    @Override
    boolean hasAttribute(String name);

    @Override
    void removeAttribute(String name);

    @Override
    Iterable<String> getAttributeNames();
}
