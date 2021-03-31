package com.jn.agileway.web.filter.prediates;

import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class MethodPredicate implements HttpServletRequestPredicate {

    private List<String> methods = Collects.emptyArrayList();

    public void setMethods(List<String> methods) {
        if (Objs.isNotEmpty(methods)) {
            this.methods = methods;
        }
    }

    @Override
    public boolean test(HttpServletRequest request) {
        String method = request.getMethod();
        return methods.contains(method);
    }
}
