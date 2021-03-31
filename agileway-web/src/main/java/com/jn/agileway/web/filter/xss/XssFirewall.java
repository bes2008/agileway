package com.jn.agileway.web.filter.xss;

import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.EmptyEvalutible;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class XssFirewall implements EmptyEvalutible, Initializable {
    private boolean inited = false;
    private List<Predicate> predicates = Collects.emptyArrayList();

    private List<XssHandler> xssHandlers = Collects.emptyArrayList();

    public void setPredicates(List<Predicate> predicates) {
        this.predicates = predicates;
    }

    public List<Predicate> getPredicates() {
        return predicates;
    }

    public void setXssHandlers(List<XssHandler> xssHandlers) {
        this.xssHandlers = xssHandlers;
    }

    public List<XssHandler> getXssHandlers() {
        return xssHandlers;
    }

    @Override
    public boolean isEmpty() {
        return Objs.isEmpty(xssHandlers);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public void init() throws InitializationException {
        if (!inited) {
            Collects.forEach(xssHandlers, new Consumer<XssHandler>() {
                @Override
                public void accept(XssHandler xssHandler) {
                    xssHandler.init();
                }
            });
            inited = true;
        }
    }

    public boolean willIntercept(HttpServletRequest request) {
        return Functions.allPredicate(Collects.toArray(predicates, Predicate[].class)).test(request);
    }
}

