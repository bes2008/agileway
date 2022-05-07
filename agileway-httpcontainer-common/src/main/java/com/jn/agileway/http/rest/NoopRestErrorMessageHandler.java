package com.jn.agileway.http.rest;

import com.jn.langx.http.rest.RestRespBody;

import java.util.Locale;

public class NoopRestErrorMessageHandler implements RestErrorMessageHandler {
    public static final NoopRestErrorMessageHandler INSTANCE = new NoopRestErrorMessageHandler();

    @Override
    public void handler(Locale locale, RestRespBody restRespBody) {
        // NOOP
    }
}
