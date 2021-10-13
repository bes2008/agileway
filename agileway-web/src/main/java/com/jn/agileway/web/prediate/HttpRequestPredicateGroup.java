package com.jn.agileway.web.prediate;

import com.jn.agileway.web.servlet.RR;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Listable;
import com.jn.langx.util.function.Functions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 用于对多个 HttpRequestPredicate 进行 and 操作
 */
public final class HttpRequestPredicateGroup implements Listable<HttpRequestPredicate> {
    private final List<HttpRequestPredicate> predicates = Collects.emptyArrayList();

    @Override
    public boolean add(HttpRequestPredicate httpRequestPredicate) {
        if (httpRequestPredicate != null) {
            return predicates.add(httpRequestPredicate);
        }
        return false;
    }

    @Override
    public boolean remove(Object httpRequestPredicate) {
        if (httpRequestPredicate != null) {
            return predicates.remove(httpRequestPredicate);
        }
        return false;
    }

    @Override
    public void clear() {
        this.predicates.clear();
    }

    @Override
    public boolean addAll(Collection<? extends HttpRequestPredicate> elements) {
        Collection es = elements;
        Collects.addAll(this.predicates, es);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        if (collection != null) {
            this.predicates.removeAll(collection);
        }
        return true;
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

    /**
     * 一个Predicate Group内的所有 Predicate都得 满足的情况下，才是匹配的
     */
    public boolean match(RR rr) {
        if (Objs.isEmpty(rr)) {
            return false;
        }
        // 没有任何的限制条件
        if (Objs.isEmpty(predicates)) {
            return true;
        }
        return Functions.allPredicate(Collects.toArray(predicates, HttpRequestPredicate[].class)).test(rr);
    }
}
