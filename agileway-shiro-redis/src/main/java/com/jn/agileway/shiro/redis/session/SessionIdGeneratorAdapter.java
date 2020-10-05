package com.jn.agileway.shiro.redis.session;

import com.jn.langx.IdGenerator;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;

public class SessionIdGeneratorAdapter implements SessionIdGenerator {
    private IdGenerator<Session> delegate;

    public SessionIdGeneratorAdapter(){}

    public SessionIdGeneratorAdapter(IdGenerator<Session> delegate){
        setDelegate(delegate);
    }

    public SessionIdGeneratorAdapter(org.springframework.util.IdGenerator uuidGenerator){
        setDelegate(new SpringIdGeneratorAdapter<Session>(uuidGenerator));
    }

    @Override
    public Serializable generateId(Session session) {
        return delegate.get(session);
    }

    public void setDelegate(IdGenerator<Session> delegate) {
        this.delegate = delegate;
    }
}
