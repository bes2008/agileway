package com.jn.agileway.web.request.handler;

import com.jn.agileway.web.servlet.RR;
import com.jn.langx.annotation.NonNull;

public interface HttpRequestHandler {
    void handle(@NonNull RR rr);
}
