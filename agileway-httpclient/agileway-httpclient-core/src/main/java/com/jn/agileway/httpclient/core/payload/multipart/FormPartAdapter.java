package com.jn.agileway.httpclient.core.payload.multipart;


public interface FormPartAdapter {
    Class[] supportedTypes();

    Object adapt(Object i);
}
