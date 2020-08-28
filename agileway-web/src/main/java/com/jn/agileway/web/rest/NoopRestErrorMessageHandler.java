package com.jn.agileway.web.rest;

import com.jn.langx.http.rest.RestRespBody;

public class NoopRestErrorMessageHandler implements RestErrorMessageHandler{
    public static final NoopRestErrorMessageHandler INSTANCE = new NoopRestErrorMessageHandler();
    @Override
    public void handler(RestRespBody restRespBody) {
        // NOOP
    }
}
