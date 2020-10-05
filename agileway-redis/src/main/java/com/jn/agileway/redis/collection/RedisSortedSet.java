package com.jn.agileway.redis.collection;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;

public class RedisSortedSet<E> implements SortedSet<ZSetOperations.TypedTuple<E>> {
    private BoundZSetOperations ops;
    private String key;

    public RedisSortedSet(RedisOperations<String, E> redisTemplate, String key) {
        this.ops = redisTemplate.boundZSetOps(key);
        this.key = key;
    }

    private final Comparator<? super ZSetOperations.TypedTuple<E>> comparator = new Comparator<ZSetOperations.TypedTuple<E>>() {
        @Override
        public int compare(ZSetOperations.TypedTuple<E> o1, ZSetOperations.TypedTuple<E> o2) {
            double delta = o1.getScore() - o2.getScore();
            if (delta == 0d) {
                return 0;
            }
            if (delta > 0) {
                return 1;
            }
            return -1;
        }
    };

    @Override
    public Comparator<? super ZSetOperations.TypedTuple<E>> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<ZSetOperations.TypedTuple<E>> subSet(ZSetOperations.TypedTuple<E> fromElement, ZSetOperations.TypedTuple<E> toElement) {
        TreeSet set = new TreeSet(comparator);
        set.addAll(ops.range(ops.rank(fromElement.getValue()), ops.rank(toElement.getValue())));
        return set;
    }

    @Override
    public SortedSet<ZSetOperations.TypedTuple<E>> headSet(ZSetOperations.TypedTuple<E> toElement) {
        ZSetOperations.TypedTuple<E> first = first();
        if (first == null) {
            return Collects.emptyTreeSet();
        }
        return subSet(first, toElement);
    }

    @Override
    public SortedSet<ZSetOperations.TypedTuple<E>> tailSet(ZSetOperations.TypedTuple<E> fromElement) {
        ZSetOperations.TypedTuple<E> last = last();
        if (last == null) {
            return Collects.emptyTreeSet();
        }
        return subSet(fromElement, last);
    }

    @Override
    public ZSetOperations.TypedTuple<E> first() {
        Set<ZSetOperations.TypedTuple<E>> set = ops.rangeWithScores(0, 0);
        if (Emptys.isNotEmpty(set)) {
            return (ZSetOperations.TypedTuple<E>) Collects.toArray(set)[0];
        }
        return null;
    }

    @Override
    public ZSetOperations.TypedTuple<E> last() {
        Set<ZSetOperations.TypedTuple<E>> set = ops.rangeWithScores(-1, -1);
        if (Emptys.isNotEmpty(set)) {
            return (ZSetOperations.TypedTuple<E>) Collects.toArray(set)[0];
        }
        return null;
    }

    @Override
    public int size() {
        return Long.valueOf(longSize()).intValue();
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
        return ops.rank(((ZSetOperations.TypedTuple) o).getValue()) != null;
    }

    @Override
    public Iterator<ZSetOperations.TypedTuple<E>> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return getAll().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        Object[] arr = toArray();
        if (a.length >= arr.length) {
            System.arraycopy(arr, 0, a, 0, arr.length);
            return a;
        } else {
            return (T[]) arr;
        }
    }

    @Override
    public boolean add(ZSetOperations.TypedTuple<E> tuple) {
        return ops.add(tuple.getValue(), tuple.getScore());
    }

    @Override
    public boolean remove(Object o) {
        ZSetOperations.TypedTuple tuple = (ZSetOperations.TypedTuple) o;
        ops.remove(tuple.getValue());
        return true;
    }

    @Override
    public boolean containsAll(Collection c) {
        return Collects.allMatch(c, new Predicate<ZSetOperations.TypedTuple<E>>() {
            @Override
            public boolean test(ZSetOperations.TypedTuple<E> tuple) {
                return contains(tuple);
            }
        });
    }

    @Override
    public boolean addAll(Collection<? extends ZSetOperations.TypedTuple<E>> c) {
        ops.add(Collects.asSet(c));
        return true;
    }

    @Override
    public boolean retainAll(Collection c) {
        final List<E> values = Pipeline.of(c).map(new Function<ZSetOperations.TypedTuple<E>, E>() {
            @Override
            public E apply(ZSetOperations.TypedTuple<E> tuple) {
                return tuple.getValue();
            }
        }).asList();

        if (Emptys.isNotEmpty(values)) {
            ops.remove(Pipeline.of(getAll()).filter(new Predicate<ZSetOperations.TypedTuple>() {
                @Override
                public boolean test(ZSetOperations.TypedTuple tuple) {
                    return !values.contains(tuple.getValue());
                }
            }).toArray());
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection c) {
        Object[] values = Pipeline.of(c).map(new Function<ZSetOperations.TypedTuple<E>, E>() {
            @Override
            public E apply(ZSetOperations.TypedTuple<E> tuple) {
                return tuple.getValue();
            }
        }).toArray();
        ops.remove(values);
        return true;
    }

    private Set<ZSetOperations.TypedTuple> getAll() {
        return ops.rangeWithScores(0, -1);
    }

    @Override
    public void clear() {
        ops.removeRange(0, -1);
    }


}
