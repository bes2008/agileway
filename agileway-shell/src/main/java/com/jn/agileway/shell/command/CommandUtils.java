package com.jn.agileway.shell.command;

import com.jn.agileway.shell.exception.MalformedCommandException;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.type.Primitives;
import org.apache.commons.cli.Converter;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public final class CommandUtils {
    private final static List<Class<?>> supportedBaseTypes;
    static {
        List<Class<?>> classes = Lists.newArrayList(Primitives.getTypes(false));
        Collects.addAll(classes, String.class, URL.class, File.class, Path.class, Class.class);
        supportedBaseTypes = Collects.immutableArrayList(classes);
    }

    public static final String NULL = "__NULL__";


    public static boolean isSupportedBasicTypes(Class type){
        return supportedBaseTypes.contains(type);
    }

    public static String getSupportedBaseTypesString(){
        return Strings.<Class<?>>join(",", null, null, supportedBaseTypes.iterator(), new Function<Class<?>, String>() {
            @Override
            public String apply(Class<?> cls) {
                return cls.getSimpleName();
            }
        },null);
    }

    public static Converter newConverter(Class converterClass, Class targetType){
        Converter converter = null;
        if(converterClass == null || converterClass == DefaultConverter.class || !Reflects.isSubClass(Converter.class, converterClass)) {
            return new DefaultConverter(targetType);
        }
        Constructor constructor = Reflects.getConstructor(converterClass, targetType);
        if(constructor==null) {
            return Reflects.<Converter>newInstance(converterClass);
        }
        else{
            return (Converter) Reflects.newInstance(constructor, targetType);
        }
    }

    public static Class getConverterTargetClass(Parameter parameter, boolean forCommandOption){
        Class parameterClass = parameter.getType();
        Class result = parameterClass;
        if (parameterClass.isArray()) {
            result = parameterClass.getComponentType();
        }
        if(forCommandOption){
            if(Reflects.isSubClassOrEquals(Collection.class, parameterClass)) {
                ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
                result = (Class) parameterizedType.getActualTypeArguments()[0];
            }
        }
        return result;
    }

    public static boolean isRequiredCommandArgument(Method method, int parameterIndex, String defaultValueString){
        boolean required = true;
        Parameter parameter = method.getParameters()[parameterIndex];
        if(parameterIndex == method.getParameterCount()-1){
            required = Reflects.hasAnnotation(parameter, NonNull.class) &&  Objs.equals(defaultValueString, NULL);
        }
        return required;
    }

    public static void checkOptionOrArgumentDefaultValues(String[] values, Converter converter, String optionOrArgumentName, String commandKey){
        // 这个过程如果没有异常，那么可以直接将 values作为 defaultValues使用
        Pipeline.of(values).map(new Function<String, Object>() {
            @Override
            public Object apply(String value) {
                try {
                    return converter.apply(value);
                } catch (Throwable e) {
                    throw new MalformedCommandException(StringTemplates.formatWithPlaceholder("Illegal defaultValues for command option or argument '{}' in command '{}'", optionOrArgumentName, commandKey));
                }
            }
        }).asList();
    }

    public static void checkOptionOrArgumentDefaultValue(String value, Converter converter, String optionOrArgumentName, String commandKey){
        // 这个过程如果没有异常，那么可以直接将 defaultValueString 作为 defaultValue使用
        try {
            converter.apply(value);
        } catch (Throwable e) {
            throw new MalformedCommandException(StringTemplates.formatWithPlaceholder("Illegal defaultValue for option '{}' in command '{}'", optionOrArgumentName, commandKey));
        }
    }

    public static boolean isNullDefaultValue(String defaultValueString){
        return Objs.equals(defaultValueString, NULL);
    }
}
