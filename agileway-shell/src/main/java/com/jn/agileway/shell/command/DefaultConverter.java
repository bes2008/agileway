package com.jn.agileway.shell.command;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.converter.ConverterService;
import org.apache.commons.cli.Converter;

import java.io.File;
import java.nio.file.Path;

public class DefaultConverter<T> implements Converter<T, IllegalArgumentException> {
    private Class<T> targetType;

    static class ConverterAdapter<T> implements com.jn.langx.Converter<String,T>{
        private Converter<T, ?> delegate;
        ConverterAdapter(Converter<T, ?> delegate){
            this.delegate =delegate;
        }

        @Override
        public T apply(String str) {
            try {
                return delegate.apply(str);
            }catch (Throwable e){
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public boolean isConvertible(Class sourceClass, Class targetClass) {
            return delegate!=null && sourceClass==String.class;
        }
    }

    private static ConverterService converterService;

    static {
        ConverterService converterService = new ConverterService();
        converterService.register(Class.class,new ConverterAdapter(Converter.CLASS));
        converterService.register(File.class, new ConverterAdapter(Converter.FILE));
        converterService.register(Path.class,new ConverterAdapter(Converter.PATH));
        converterService.register(java.net.URL.class, new ConverterAdapter(Converter.URL));
        DefaultConverter.converterService = converterService;
    }

    public DefaultConverter(Class targetType){
        Preconditions.checkNotEmpty(targetType);
        this.targetType = targetType;
        if(null==converterService.findConverter(String.class,targetType)){
            throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("illegal type: {}", targetType));
        }
    }

    @Override
    public T apply(String string) throws IllegalArgumentException {
        Preconditions.checkArgument(string != null);
        com.jn.langx.Converter langxConverter = converterService.findConverter(String.class,targetType);
        try {
            return (T)langxConverter.apply(string);
        }catch (Throwable e){
            throw new IllegalArgumentException(e);
        }
    }
}
