package com.jn.agileway.web.prediate;

import com.jn.langx.Factory;
import com.jn.langx.Named;

public abstract class HttpRequestPredicateFactory<P extends HttpRequestPredicate> implements Factory<String, P>, Named {
    private String name;

    @Override
    public abstract P get(String configuration);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
