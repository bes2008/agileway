package com.jn.agileway.jwt;


import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.MapAccessor;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.collection.Sets;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.valuegetter.ValueGetter2;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class KeyValueSet implements ValueGetter2<String> {
    private MapAccessor keyvalueAccessor;

    public KeyValueSet(Map<String, Object> map) {
        if (map == null) {
            map = Maps.newHashMap();
        }
        this.keyvalueAccessor = new MapAccessor(new HashMap<String, Object>(map));
    }

    protected final Map<String, Object> getAll() {
        return Collects.immutableMap(keyvalueAccessor.getTarget());
    }

    protected final Set<String> getKeys() {
        return Sets.immutableSet(getAll().keySet());
    }

    @Override
    public Object get(String key) {
        return keyvalueAccessor.get(key);
    }

    @Override
    public boolean isNull(String key) {
        return keyvalueAccessor.isNull(key);
    }

    @Override
    public boolean isEmpty(String key) {
        return keyvalueAccessor.isEmpty(key);
    }

    @Override
    public boolean has(String key) {
        return keyvalueAccessor.has(key);
    }

    @Override
    public String getString(String key) {
        return keyvalueAccessor.getString(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return keyvalueAccessor.getString(key, defaultValue);
    }

    @Override
    public Character getCharacter(String key) {
        return keyvalueAccessor.getCharacter(key);
    }

    @Override
    public Character getCharacter(String key, Character defaultValue) {
        return keyvalueAccessor.getCharacter(key);
    }

    @Override
    public Byte getByte(String key) {
        return keyvalueAccessor.getByte(key);
    }

    @Override
    public Byte getByte(String key, Byte defaultValue) {
        return keyvalueAccessor.getByte(key, defaultValue);
    }

    @Override
    public Short getShort(String key) {
        return keyvalueAccessor.getShort(key);
    }

    @Override
    public Short getShort(String key, Short defaultValue) {
        return keyvalueAccessor.getShort(key, defaultValue);
    }

    @Override
    public Integer getInteger(String key) {
        return keyvalueAccessor.getInteger(key);
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return keyvalueAccessor.getInteger(key, defaultValue);
    }

    @Override
    public Double getDouble(String key) {
        return keyvalueAccessor.getDouble(key);
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        return keyvalueAccessor.getDouble(key, defaultValue);
    }

    @Override
    public Float getFloat(String key) {
        return keyvalueAccessor.getFloat(key);
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        return keyvalueAccessor.getFloat(key, defaultValue);
    }

    @Override
    public Long getLong(String key) {
        return keyvalueAccessor.getLong(key);
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return keyvalueAccessor.getLong(key, defaultValue);
    }

    @Override
    public Boolean getBoolean(String key) {
        return keyvalueAccessor.getBoolean(key);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return keyvalueAccessor.getBoolean(key, defaultValue);
    }

    @Override
    public Object get(String key, Function<Object, Object> mapper) {
        return keyvalueAccessor.get(key, mapper);
    }

    @Override
    public String getString(String key, Function<Object, String> mapper) {
        return keyvalueAccessor.getString(key, mapper);
    }

    @Override
    public Character getCharacter(String key, Function<Object, Character> mapper) {
        return keyvalueAccessor.getCharacter(key, mapper);
    }

    @Override
    public Byte getByte(String key, Function<Object, Byte> mapper) {
        return keyvalueAccessor.getByte(key, mapper);
    }

    @Override
    public Short getShort(String key, Function<Object, Short> mapper) {
        return keyvalueAccessor.getShort(key, mapper);
    }

    @Override
    public Integer getInteger(String key, Function<Object, Integer> mapper) {
        return keyvalueAccessor.getInteger(key, mapper);
    }

    @Override
    public Double getDouble(String key, Function<Object, Double> mapper) {
        return keyvalueAccessor.getDouble(key, mapper);
    }

    @Override
    public Float getFloat(String key, Function<Object, Float> mapper) {
        return keyvalueAccessor.getFloat(key, mapper);
    }

    @Override
    public Long getLong(String key, Function<Object, Long> mapper) {
        return keyvalueAccessor.getLong(key, mapper);
    }

    @Override
    public Boolean getBoolean(String key, Function<Object, Boolean> mapper) {
        return keyvalueAccessor.getBoolean(key, mapper);
    }

    @Override
    public <E> E getAny(String... keys) {
        return keyvalueAccessor.getAny(keys);
    }

    @Override
    public <E> E getAny(Predicate2<String, E> predicate, String... keys) {
        return keyvalueAccessor.getAny(predicate, keys);
    }
}
