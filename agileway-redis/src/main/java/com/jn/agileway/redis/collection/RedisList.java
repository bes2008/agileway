package com.jn.agileway.redis.collection;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisOperations;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class RedisList<E> implements List<E> {
    private BoundListOperations ops;
    private String key;

    public RedisList(RedisOperations<String, List<E>> redisTemplate, String key) {
        this.key = key;
        ops = redisTemplate.boundListOps(key);
    }

    @Override
    public int size() {
        return Long.valueOf(ops.size()).intValue();
    }

    public long longSize() {
        return ops.size();
    }


    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object value) {
        return indexOf(value) != -1;
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public Object[] toArray() {
        return getAll().toArray();
    }

    @Override
    public <E> E[] toArray(E[] a) {
        return getAll().toArray(a);
    }

    private Collection<E> getAll() {
        return ops.range(0, -1);
    }

    @Override
    public boolean add(E e) {
        ops.rightPush(e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        ops.remove(0, o);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return Collects.anyMatch(c, new Predicate() {
            @Override
            public boolean test(Object value) {
                return contains(value);
            }
        });
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        ops.rightPushAll(Collects.toArray(c));
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        final E value = get(index);
        Collects.forEach(c, new Consumer<E>() {
            @Override
            public void accept(E e) {
                ops.leftPush(value, e);
            }
        });
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Collects.forEach(c, new Consumer() {
            @Override
            public void accept(Object o) {
                remove(o);
            }
        });
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // step 1: store all exists elements
        final List<E> retainedList = Collects.emptyLinkedList();
        Collects.forEach(c, new Consumer<E>() {
            @Override
            public void accept(E e) {
                long removed = ops.remove(0, e);
                for (long j = 0L; j < removed; j++) {
                    retainedList.add(e);
                }
            }
        });
        // step 2: clear all
        clear();

        // step 3: push all retained elements
        addAll(retainedList);
        return true;
    }

    @Override
    public void clear() {
        ops.trim(0, -1);
    }

    @Override
    public E get(int index) {
        return (E) ops.index(index);
    }

    @Override
    public E set(int index, E element) {
        E old = get(index);
        ops.set(index, element);
        return old;
    }

    @Override
    public void add(int index, E element) {
        E e = get(index);
        ops.leftPush(e, element);
    }

    @Override
    public E remove(int index) {
        E e = get(index);
        List<E> sublist = subList(index + 1, -1);
        ops.trim(index, -1);
        if (Emptys.isNotEmpty(sublist)) {
            addAll(sublist);
        }
        return e;
    }

    @Override
    public int indexOf(Object o) {
        long size = longSize();
        if (size == 0) {
            return -1;
        }
        int pageSize = 100;
        int pageNo = 1;
        long maxPage = size % pageSize == 0 ? (size / pageSize + 1) : (size / pageSize);
        while (pageNo <= maxPage) {
            long offset = (pageNo - 1) * pageSize;
            List<E> partition = ops.range(offset, offset + pageSize - 1);
            if (Emptys.isNotEmpty(partition)) {
                int index = partition.indexOf(o);
                if (index >= 0) {
                    return Long.valueOf(offset + index).intValue();
                }
                pageNo = pageNo + 1;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        long size = longSize();
        if (size == 0) {
            return -1;
        }
        int pageSize = 100;
        long maxPage = size % pageSize == 0 ? (size / pageSize + 1) : (size / pageSize);
        long pageNo = maxPage;
        while (pageNo >= 1) {
            long offset = (pageNo - 1) * pageSize;
            List<E> partition = ops.range(offset, offset + pageSize - 1);
            if (Emptys.isNotEmpty(partition)) {
                int index = partition.lastIndexOf(o);
                if (index >= 0) {
                    return Long.valueOf(offset + index).intValue();
                }
                pageNo = pageNo - 1;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new RedisListIterator(index, longSize());
    }

    class RedisListIterator implements ListIterator {
        private int index = 0;
        private long size = 0L;

        RedisListIterator(int index, long size) {
            this.index = index;
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public Object next() {
            return get(index++);
        }

        @Override
        public boolean hasPrevious() {
            return true;
        }

        @Override
        public Object previous() {
            return get(index - 1);
        }

        @Override
        public int nextIndex() {
            return index + 1;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            RedisList.this.remove(index);
        }

        @Override
        public void set(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Object o) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return ops.range(fromIndex, toIndex - 1);
    }
}
