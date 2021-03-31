package com.jn.agileway.web.filter.xss;

import com.jn.agileway.web.prediates.HttpRequestPredicate;
import com.jn.agileway.web.servlet.RR;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Listable;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Functions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class XssFirewall implements Listable<XssHandler>, Initializable {
    private boolean inited = false;
    private List<HttpRequestPredicate> predicates = Collects.emptyArrayList();

    private List<XssHandler> xssHandlers = Collects.emptyArrayList();

    public void setPredicates(List<HttpRequestPredicate> predicates) {
        this.predicates = predicates;
    }

    public List<HttpRequestPredicate> getPredicates() {
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

    public void addPredicate(HttpRequestPredicate predicate) {
        Collects.addAll(predicates, predicate);
    }

    @Override
    public void add(XssHandler xssHandler) {
        if (xssHandler != null) {
            xssHandlers.add(xssHandler);
        }
    }

    @Override
    public void remove(XssHandler xssHandler) {
        if (xssHandler != null) {
            xssHandlers.remove(xssHandler);
        }
    }

    @Override
    public void clear(XssHandler xssHandler) {
        xssHandlers.clear();
    }

    @Override
    public void addAll(Collection<XssHandler> collection) {
        Collects.addAll(xssHandlers, collection);
    }

    @Override
    public Iterator<XssHandler> iterator() {
        return xssHandlers.iterator();
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

    public boolean willIntercept(RR holder) {
        return Functions.allPredicate(Collects.toArray(predicates, HttpRequestPredicate[].class)).test(holder);
    }
}

