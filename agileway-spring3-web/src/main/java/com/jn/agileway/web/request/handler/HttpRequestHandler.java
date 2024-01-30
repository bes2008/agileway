package com.jn.agileway.web.request.handler;

import com.jn.agileway.http.rr.RR;
import com.jn.langx.annotation.NonNull;

public interface HttpRequestHandler {
    void handle(@NonNull RR rr);
}
