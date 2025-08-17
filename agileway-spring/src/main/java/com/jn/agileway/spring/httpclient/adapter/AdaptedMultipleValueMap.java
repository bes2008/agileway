package com.jn.agileway.spring.httpclient.adapter;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import org.springframework.util.MultiValueMap;

import java.util.*;

public class AdaptedMultipleValueMap<K, V> implements com.jn.langx.util.collection.multivalue.MultiValueMap<K, V> {
    private MultiValueMap<K, V> delegate;

    public AdaptedMultipleValueMap(MultiValueMap<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public V getFirst(K k) {
        return this.delegate.getFirst(k);
    }

    @Override
    public V getValue(K k, int i) {
        List<V> values = this.delegate.get(k);
        return values.get(i);
    }

    @Override
    public void add(K k, V v) {
        this.delegate.add(k, v);
    }

    @Override
    public void addAll(K k, Collection<? extends V> collection) {
        this.delegate.addAll(k, Collects.asList(collection));
    }

    @Override
    public void addAll(K k, V[] vs) {
        this.delegate.addAll(k, Collects.asList(vs));
    }

    @Override
    public void addAll(com.jn.langx.util.collection.multivalue.MultiValueMap<K, V> multiValueMap) {
        for (Map.Entry<K, Collection<V>> entry : multiValueMap.entrySet()) {
            this.addAll(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void addIfAbsent(K k, V v) {
        this.delegate.addIfAbsent(k, v);
    }

    @Override
    public void addIfValueAbsent(K k, V v) {
        List<V> values = this.delegate.get(k);
        if (values == null || !values.contains(v)) {
            this.add(k, v);
        }
    }

    @Override
    public void removeValue(K k, V v) {
        this.delegate.remove(k, v);
    }

    @Override
    public void set(K k, V v) {
        this.delegate.remove(k);
        this.add(k, v);
    }

    @Override
    public void setAll(Map<K, V> map) {
        this.delegate.clear();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            this.add(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Map<K, V> toSingleValueMap() {
        return Collections.emptyMap();
    }

    @Override
    public Map<K, List<V>> toMap() {
        return Collections.emptyMap();
    }

    @Override
    public int total() {
        Collection<Collection<V>> values = this.values();
        return Pipeline.of(values).map(new Function<Collection<V>, Integer>() {
            @Override
            public Integer apply(Collection<V> vs) {
                return vs.size();
            }
        }).sum().intValue();
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        Collection<Collection<V>> values = this.values();
        return Pipeline.of(values)
                .anyMatch(new Predicate<Collection<V>>() {
                    @Override
                    public boolean test(Collection<V> vs) {
                        return vs.contains(value);
                    }
                });
    }

    @Override
    public Collection<V> get(Object key) {
        return this.delegate.get(key);
    }

    @Override
    public Collection<V> put(K key, Collection<V> value) {
        Collection<V> old = this.remove(key);
        this.delegate.addAll(key, Collects.asList(value));
        return old;
    }

    @Override
    public Collection<V> remove(Object key) {
        return this.delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Collection<V>> m) {
        for (Map.Entry<? extends K, ? extends Collection<V>> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.delegate.keySet();
    }

    @Override
    public Collection<Collection<V>> values() {
        Collection<List<V>> r = this.delegate.values();
        Collection<Collection<V>> result = new ArrayList<Collection<V>>();
        for (List<V> vs : r) {
            result.add(vs);
        }
        return result;
    }

    @Override
    public Set<Entry<K, Collection<V>>> entrySet() {
        Set<Entry<K, List<V>>> r = this.delegate.entrySet();
        Set<Entry<K, Collection<V>>> result = new HashSet<Entry<K, Collection<V>>>();
        for (Entry<K, List<V>> entry : r) {
            result.add(new Entry<K, Collection<V>>() {
                @Override
                public K getKey() {
                    return entry.getKey();
                }

                @Override
                public Collection<V> getValue() {
                    return entry.getValue();
                }

                @Override
                public Collection<V> setValue(Collection<V> value) {
                    return AdaptedMultipleValueMap.this.put(entry.getKey(), value);
                }
            });
        }
        return result;
    }
}
