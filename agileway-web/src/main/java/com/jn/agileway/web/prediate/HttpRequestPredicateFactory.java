package com.jn.agileway.web.prediate;

import com.jn.langx.Named;
import com.jn.langx.factory.Factory;

public abstract class HttpRequestPredicateFactory implements Factory<String, HttpRequestPredicate>, Named {
    private String name;

    @Override
    public abstract HttpRequestPredicate get(String configuration);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
