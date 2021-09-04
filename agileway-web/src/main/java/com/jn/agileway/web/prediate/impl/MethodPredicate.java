package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicate;
import com.jn.agileway.web.servlet.RR;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.net.http.HttpMethod;

import java.util.List;

public class MethodPredicate implements HttpRequestPredicate {
    static final List<String> DEFAULT_METHODS = Pipeline.of(HttpMethod.values()).map(new Function<HttpMethod, String>() {
        @Override
        public String apply(HttpMethod httpMethod) {
            return httpMethod.name();
        }
    }).asList();

    private List<String> methods = DEFAULT_METHODS;

    public void setMethods(String... methods) {
        setMethods(Collects.asList(methods));
    }

    public void setMethods(List<String> methods) {
        if (Objs.isNotEmpty(methods)) {
            this.methods = methods;
        }
    }

    @Override
    public boolean test(RR holder) {
        String method = holder.getRequest().getMethod();
        return methods.contains(method);
    }
}
