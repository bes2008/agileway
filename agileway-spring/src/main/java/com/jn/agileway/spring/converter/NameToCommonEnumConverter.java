package com.jn.agileway.spring.converter;

import com.jn.langx.util.enums.CommonEnumByNameConverter;
import com.jn.langx.util.enums.base.CommonEnum;
import org.springframework.core.convert.converter.Converter;

public class NameToCommonEnumConverter<T extends CommonEnum> implements Converter<String, T> {

    private CommonEnumByNameConverter<T> delegate;

    public NameToCommonEnumConverter(Class enumClass) {
        this.delegate = new CommonEnumByNameConverter(enumClass);
    }

    @Override
    public T convert(String name) {
        if (!delegate.isConvertible(String.class, this.delegate.getEnumClass())) {
            return null;
        }
        return this.delegate.apply(name);
    }
}
