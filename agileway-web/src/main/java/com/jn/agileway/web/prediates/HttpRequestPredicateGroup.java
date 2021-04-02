package com.jn.agileway.web.prediates;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Listable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class HttpRequestPredicateGroup implements Listable<HttpRequestPredicate> {
    private final List<HttpRequestPredicate> predicates = Collects.emptyArrayList();

    @Override
    public void add(HttpRequestPredicate httpRequestPredicate) {
        if (httpRequestPredicate != null) {
            predicates.add(httpRequestPredicate);
        }
    }

    @Override
    public void remove(HttpRequestPredicate httpRequestPredicate) {
        if (httpRequestPredicate != null) {
            predicates.remove(httpRequestPredicate);
        }
    }

    @Override
    public void clear(HttpRequestPredicate httpRequestPredicate) {
        this.predicates.clear();
    }

    @Override
    public void addAll(Collection<HttpRequestPredicate> elements) {
        Collects.addAll(this.predicates, elements);
    }

    @Override
    public Iterator<HttpRequestPredicate> iterator() {
        return predicates.iterator();
    }

    @Override
    public boolean isEmpty() {
        return predicates.isEmpty();
    }

    @Override
    public boolean isNull() {
        return false;
    }
}
