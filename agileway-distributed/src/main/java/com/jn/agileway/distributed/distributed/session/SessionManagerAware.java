package com.jn.agileway.distributed.distributed.session;

public interface SessionManagerAware {
    SessionManager getSessionManager();

    void setSessionManager(SessionManager sessionManager);
}
