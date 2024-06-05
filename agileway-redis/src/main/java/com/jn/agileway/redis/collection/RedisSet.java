package com.jn.agileway.redis.collection;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisOperations;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RedisSet<E> implements Set<E> {
    private BoundSetOperations ops;
    private String key;

    public RedisSet(RedisOperations<String, E> template, String key) {
        this.ops = template.boundSetOps(key);
        this.key = key;
    }

    @Override
    public int size() {
        return (int)longSize();
    }

    public long longSize() {
        return ops.size();
    }

    @Override
    public boolean isEmpty() {
        return longSize() == 0L;
    }

    @Override
    public boolean contains(Object o) {
        return ops.isMember(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new RedisSetIterator();
    }

    @Override
    public Object[] toArray() {
        return ops.members().toArray();
    }

    @Override
    public <E> E[] toArray(E[] a) {
        return (E[]) ops.members().toArray(a);
    }

    @Override
    public boolean add(E e) {
        ops.add(e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        ops.remove(o);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return ops.members().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Object[] elements = Collects.toArray(c);
        ops.add(elements);
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return Collects.allMatch(c, new Predicate() {
            @Override
            public boolean test(Object o) {
                return ops.isMember(o);
            }
        });
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Object[] elements = Collects.toArray(c);
        ops.remove(elements);
        return true;
    }

    @Override
    public void clear() {
        removeAll(ops.members());
    }

    class RedisSetIterator implements Iterator {
        List<E> list;
        int index = 0;

        public RedisSetIterator() {
            list = Collects.asList(ops.members());
        }

        @Override
        public boolean hasNext() {
            return !list.isEmpty();
        }

        @Override
        public Object next() {
            return list.get(index++);
        }

        @Override
        public void remove() {
            if (index < list.size()) {
                Object obj = list.remove(index);
                ops.remove(obj);
            }
        }
    }
}
