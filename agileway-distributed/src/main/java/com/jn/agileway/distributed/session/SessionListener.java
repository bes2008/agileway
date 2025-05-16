package com.jn.agileway.distributed.session;

import com.jn.langx.event.EventListener;

public interface SessionListener extends EventListener<SessionEvent> {
    @Override
    void on(SessionEvent event);
}
