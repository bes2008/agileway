package com.jn.agileway.distributed.session;

public interface SessionManagerAware {
    SessionManager getSessionManager();

    void setSessionManager(SessionManager sessionManager);
}
