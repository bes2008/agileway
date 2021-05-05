package com.jn.agileway.web.security;

import com.jn.agileway.web.prediate.HttpRequestPredicateGroup;
import com.jn.agileway.web.servlet.RR;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Listable;
import com.jn.langx.util.function.Consumer;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class WAFStrategy implements Listable<WAFHandler>, Initializable {
    private HttpRequestPredicateGroup predicates = null;
    private final List<WAFHandler> handlers = Collects.emptyArrayList();


    public List<WAFHandler> getHandlers() {
        return handlers;
    }

    public boolean match(RR rr) {
        return predicates != null && predicates.match(rr);
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

    public void setPredicates(HttpRequestPredicateGroup predicates) {
        this.predicates = predicates;
    }
}
