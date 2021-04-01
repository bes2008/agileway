package com.jn.agileway.web.filter.waf;

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

public class WAFStrategy implements Listable<WAFHandler>, Initializable {
    private final List<HttpRequestPredicate> predicates = Collects.emptyArrayList();
    private final List<WAFHandler> handlers = Collects.emptyArrayList();

    public List<HttpRequestPredicate> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<HttpRequestPredicate> predicates) {
        Collects.addAll(this.predicates, predicates);
    }

    public void addPredicate(HttpRequestPredicate predicate) {
        Collects.addAll(this.predicates, predicate);
    }

    public List<WAFHandler> getHandlers() {
        return handlers;
    }

    public boolean match(RR rr) {
        if (Objs.isEmpty(rr)) {
            return false;
        }
        return Functions.allPredicate(Collects.toArray(predicates, HttpRequestPredicate[].class)).test(rr);
    }

    @Override
    public void init() throws InitializationException {
        Collects.forEach(this.handlers, new Consumer<WAFHandler>() {
            @Override
            public void accept(WAFHandler wafHandler) {
                wafHandler.init();
            }
        });
    }

    @Override
    public void add(WAFHandler wafHandler) {
        this.handlers.add(wafHandler);
    }

    @Override
    public void remove(WAFHandler wafHandler) {
        this.handlers.remove(wafHandler);
    }

    @Override
    public void clear(WAFHandler wafHandler) {
        this.handlers.clear();
    }

    @Override
    public void addAll(Collection<WAFHandler> collection) {
        Collects.addAll(this.handlers, collection);
    }

    @Override
    public Iterator<WAFHandler> iterator() {
        return this.handlers.iterator();
    }

    @Override
    public boolean isEmpty() {
        return Objs.isEmpty(handlers);
    }

    @Override
    public boolean isNull() {
        return handlers == null;
    }
}
