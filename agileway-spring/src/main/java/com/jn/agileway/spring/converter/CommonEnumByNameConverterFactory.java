package com.jn.agileway.spring.converter;

import com.jn.langx.registry.Registry;
import com.jn.langx.util.enums.base.CommonEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.concurrent.ConcurrentHashMap;

public class CommonEnumByNameConverterFactory<T extends CommonEnum> implements Registry<Class, Converter<String, T>>, ConverterFactory<String, CommonEnum> {
    private ConcurrentHashMap<Class, Converter<String, T>> cache;
    public CommonEnumByNameConverterFactory(){
        this.cache= new ConcurrentHashMap<>();
    }
    @Override
    public void register(Converter<String, T> stringTConverter) {
        // noop
    }

    @Override
    public void register(Class enumClass, Converter<String, T> stringTConverter) {
        cache.put(enumClass, stringTConverter);
    }

    @Override
    public Converter<String, T> get(Class enumClass) {
        return cache.get(enumClass);
    }

    @Override
    public void unregister(Class key) {
        cache.remove(key);
    }

    @Override
    public boolean contains(Class key) {
        return cache.containsKey(key);
    }

    @Override
    public <T extends CommonEnum> Converter<String, T> getConverter(Class<T> targetType) {
        if (targetType.isEnum()) {
            Converter converter = get(targetType);
            if (converter == null) {
                converter = new NameToCommonEnumConverter(targetType);
                register(targetType, converter);
            }
            return converter;
        }
        return null;
    }
}
