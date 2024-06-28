package com.jn.agileway.shiro.redis.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;

public class SimpleShiroSessionFactory implements SessionFactory {
    @Override
    public Session createSession(SessionContext ctx) {
        if (ctx != null) {
            String host = ctx.getHost();
            if (host != null) {
                return new SimpleShiroSession(host);
            }
        }
        return new SimpleShiroSession();
    }
}
