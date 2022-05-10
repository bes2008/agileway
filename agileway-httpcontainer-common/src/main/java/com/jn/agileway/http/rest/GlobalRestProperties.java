package com.jn.agileway.http.rest;

// prefix: agileway.rest
public class GlobalRestProperties {
    private GlobalRestResponseBodyHandlerProperties globalResponseBody = new GlobalRestResponseBodyHandlerProperties();
    private GlobalRestExceptionHandlerProperties globalExceptionHandler = new GlobalRestExceptionHandlerProperties();

    public GlobalRestResponseBodyHandlerProperties getGlobalResponseBody() {
        return globalResponseBody;
    }

    public void setGlobalResponseBody(GlobalRestResponseBodyHandlerProperties globalResponseBody) {
        this.globalResponseBody = globalResponseBody;
    }

    public GlobalRestExceptionHandlerProperties getGlobalExceptionHandler() {
        return globalExceptionHandler;
    }

    public void setGlobalExceptionHandler(GlobalRestExceptionHandlerProperties globalExceptionHandler) {
        this.globalExceptionHandler = globalExceptionHandler;
    }
}
