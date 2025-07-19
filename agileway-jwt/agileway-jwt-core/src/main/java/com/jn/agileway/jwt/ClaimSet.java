package com.jn.agileway.jwt;


import com.jn.langx.util.collection.MapAccessor;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.valuegetter.ValueGetter2;

import java.util.HashMap;
import java.util.Map;

class ClaimSet implements ValueGetter2<String> {
    private MapAccessor claimsAccessor;

    public ClaimSet(Map<String, Object> map) {
        if (map == null) {
            map = Maps.newHashMap();
        }
        this.claimsAccessor = new MapAccessor(new HashMap<String, Object>(map));
    }

    public final Map<String, Object> getAllClaims() {
        return claimsAccessor.getTarget();
    }

    @Override
    public Object get(String key) {
        return claimsAccessor.get(key);
    }

    @Override
    public boolean isNull(String key) {
        return claimsAccessor.isNull(key);
    }

    @Override
    public boolean isEmpty(String key) {
        return claimsAccessor.isEmpty(key);
    }

    @Override
    public boolean has(String key) {
        return claimsAccessor.has(key);
    }

    @Override
    public String getString(String key) {
        return claimsAccessor.getString(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return claimsAccessor.getString(key, defaultValue);
    }

    @Override
    public Character getCharacter(String key) {
        return claimsAccessor.getCharacter(key);
    }

    @Override
    public Character getCharacter(String key, Character defaultValue) {
        return claimsAccessor.getCharacter(key);
    }

    @Override
    public Byte getByte(String key) {
        return claimsAccessor.getByte(key);
    }

    @Override
    public Byte getByte(String key, Byte defaultValue) {
        return claimsAccessor.getByte(key, defaultValue);
    }

    @Override
    public Short getShort(String key) {
        return claimsAccessor.getShort(key);
    }

    @Override
    public Short getShort(String key, Short defaultValue) {
        return claimsAccessor.getShort(key, defaultValue);
    }

    @Override
    public Integer getInteger(String key) {
        return claimsAccessor.getInteger(key);
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return claimsAccessor.getInteger(key, defaultValue);
    }

    @Override
    public Double getDouble(String key) {
        return claimsAccessor.getDouble(key);
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        return claimsAccessor.getDouble(key, defaultValue);
    }

    @Override
    public Float getFloat(String key) {
        return claimsAccessor.getFloat(key);
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        return claimsAccessor.getFloat(key, defaultValue);
    }

    @Override
    public Long getLong(String key) {
        return claimsAccessor.getLong(key);
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return claimsAccessor.getLong(key, defaultValue);
    }

    @Override
    public Boolean getBoolean(String key) {
        return claimsAccessor.getBoolean(key);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return claimsAccessor.getBoolean(key, defaultValue);
    }

    @Override
    public Object get(String key, Function<Object, Object> mapper) {
        return claimsAccessor.get(key, mapper);
    }

    @Override
    public String getString(String key, Function<Object, String> mapper) {
        return claimsAccessor.getString(key, mapper);
    }

    @Override
    public Character getCharacter(String key, Function<Object, Character> mapper) {
        return claimsAccessor.getCharacter(key, mapper);
    }

    @Override
    public Byte getByte(String key, Function<Object, Byte> mapper) {
        return claimsAccessor.getByte(key, mapper);
    }

    @Override
    public Short getShort(String key, Function<Object, Short> mapper) {
        return claimsAccessor.getShort(key, mapper);
    }

    @Override
    public Integer getInteger(String key, Function<Object, Integer> mapper) {
        return claimsAccessor.getInteger(key, mapper);
    }

    @Override
    public Double getDouble(String key, Function<Object, Double> mapper) {
        return claimsAccessor.getDouble(key, mapper);
    }

    @Override
    public Float getFloat(String key, Function<Object, Float> mapper) {
        return claimsAccessor.getFloat(key, mapper);
    }

    @Override
    public Long getLong(String key, Function<Object, Long> mapper) {
        return claimsAccessor.getLong(key, mapper);
    }

    @Override
    public Boolean getBoolean(String key, Function<Object, Boolean> mapper) {
        return claimsAccessor.getBoolean(key, mapper);
    }

    @Override
    public <E> E getAny(String... keys) {
        return claimsAccessor.getAny(keys);
    }

    @Override
    public <E> E getAny(Predicate2<String, E> predicate, String... keys) {
        return claimsAccessor.getAny(predicate, keys);
    }
}
