package com.jn.agileway.web.prediates;

import com.jn.agileway.web.servlet.RR;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;

import java.util.List;

public class MethodPredicate implements HttpRequestPredicate {

    private List<String> methods = Collects.emptyArrayList();

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
