package com.jn.agileway.jwt;


import com.jn.langx.util.collection.MapAccessor;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.valuegetter.ValueGetter2;

import java.util.Map;

public class ClaimSet implements ValueGetter2<String> {
    private MapAccessor claimsAccessor;
    public ClaimSet(Map<String, Object> map){
        if(map==null){
            map= Maps.newHashMap();
        }
        this.claimsAccessor=new MapAccessor(map);
    }

    public final Map<String,Object> getAllClaims(){
        return claimsAccessor.getTarget();
    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public boolean isNull(String key) {
        return false;
    }

    @Override
    public boolean isEmpty(String key) {
        return false;
    }

    @Override
    public boolean has(String key) {
        return false;
    }

    @Override
    public Object get(String key, Function<Object, Object> mapper) {
        return null;
    }

    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public String getString(String key, String defaultValue) {
        return null;
    }

    @Override
    public String getString(String key, Function<Object, String> mapper) {
        return null;
    }

    @Override
    public Character getCharacter(String key) {
        return null;
    }

    @Override
    public Character getCharacter(String key, Character defaultValue) {
        return null;
    }

    @Override
    public Character getCharacter(String key, Function<Object, Character> mapper) {
        return null;
    }

    @Override
    public Byte getByte(String key) {
        return null;
    }

    @Override
    public Byte getByte(String key, Byte defaultValue) {
        return null;
    }

    @Override
    public Byte getByte(String key, Function<Object, Byte> mapper) {
        return null;
    }

    @Override
    public Short getShort(String key) {
        return null;
    }

    @Override
    public Short getShort(String key, Short defaultValue) {
        return null;
    }

    @Override
    public Short getShort(String key, Function<Object, Short> mapper) {
        return null;
    }

    @Override
    public Integer getInteger(String key) {
        return null;
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return null;
    }

    @Override
    public Integer getInteger(String key, Function<Object, Integer> mapper) {
        return null;
    }

    @Override
    public Double getDouble(String key) {
        return null;
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        return null;
    }

    @Override
    public Double getDouble(String key, Function<Object, Double> mapper) {
        return null;
    }

    @Override
    public Float getFloat(String key) {
        return null;
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        return null;
    }

    @Override
    public Float getFloat(String key, Function<Object, Float> mapper) {
        return null;
    }

    @Override
    public Long getLong(String key) {
        return null;
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return null;
    }

    @Override
    public Long getLong(String key, Function<Object, Long> mapper) {
        return null;
    }

    @Override
    public Boolean getBoolean(String key) {
        return null;
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return null;
    }

    @Override
    public Boolean getBoolean(String key, Function<Object, Boolean> mapper) {
        return null;
    }

    @Override
    public <E> E getAny(String... keys) {
        return null;
    }

    @Override
    public <E> E getAny(Predicate2<String, E> predicate, String... keys) {
        return null;
    }
}
