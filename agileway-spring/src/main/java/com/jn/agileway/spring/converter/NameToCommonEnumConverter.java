package com.jn.agileway.spring.converter;

import com.jn.langx.util.enums.CommonEnumByNameConverter;
import com.jn.langx.util.enums.base.CommonEnum;
import org.springframework.core.convert.converter.Converter;

/**
 * 主要用于http Get 参数去转换到 enum 的情况
 */
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
