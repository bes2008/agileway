package com.jn.agileway.distributed.session;

import com.jn.langx.Factory;

/**
 * @since 3.7.0
 */
public interface SessionFactory extends Factory<SessionContext, Session> {
    @Override
    Session get(SessionContext ctx);

    SessionIdGenerator<SessionContext> getIdGenerator();
    void setIdGenerator(SessionIdGenerator<SessionContext> idGenerator);
}
