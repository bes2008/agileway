package com.jn.agileway.httpclient.core.payload.multipart;

import java.util.List;

public interface FormPartAdapter {
    List<Class> supportedTypes();

    Object adapt(Object i);
}
