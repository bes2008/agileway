package com.jn.agileway.spring.httpclient.adapter;

import com.jn.agileway.httpclient.core.payload.multipart.FormPartAdapter;
import org.springframework.core.io.Resource;

public class SpringResourceAdapter implements FormPartAdapter {
    @Override
    public Class[] supportedTypes() {
        return new Class[]{Resource.class};
    }

    public Object adapt(Object i) {
        if (i instanceof Resource) {
            return new AdaptedResource((Resource) i);
        }
        return i;
    }
}
