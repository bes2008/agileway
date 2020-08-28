package com.jn.agileway.web.rest;

import com.jn.langx.Builder;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.List;

public class GlobalRestResponseBodyHandlerConfigurationBuilder implements Builder<GlobalRestResponseBodyHandlerConfiguration> {
    private static final Logger logger = LoggerFactory.getLogger(GlobalRestResponseBodyHandlerConfigurationBuilder.class);
    private GlobalRestResponseBodyHandlerProperties properties;

    public GlobalRestResponseBodyHandlerConfigurationBuilder setProperties(GlobalRestResponseBodyHandlerProperties properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public GlobalRestResponseBodyHandlerConfiguration build() {
        GlobalRestResponseBodyHandlerConfiguration configuration = new GlobalRestResponseBodyHandlerConfiguration();

        if (properties == null) {
            return configuration;
        }

        configuration.setAnnotations(loadAnnotationClasses(properties.getAnnotations()));
        configuration.setExcludedAnnotations(loadAnnotationClasses(properties.getExcludedAnnotations()));
        configuration.setAssignableTypes(loadClasses(properties.getAssignableTypes()));
        configuration.setExcludedAssignableTypes(loadClasses(properties.getExcludedAssignableTypes()));
        configuration.setBasePackages(properties.getBasePackages());
        configuration.setExcludedBasePackages(properties.getExcludedBasePackages());
        return configuration;
    }

    private List<Class> loadClasses(List<String> classNames){
        return Pipeline.of(classNames).map(new Function<String, Class>() {
            @Override
            public Class apply(String annotationClassName) {
                Class clazz = null;
                try {
                    clazz = ClassLoaders.loadClass(annotationClassName);
                }catch (ClassNotFoundException ex){
                    logger.error(ex.getMessage());
                }
                return clazz;
            }
        }).clearNulls().asList();
    }

    private List<Class> loadAnnotationClasses(List<String> classNames){
        return Pipeline.of(classNames).map(new Function<String, Class>() {
            @Override
            public Class apply(String annotationClassName) {
                Class clazz = null;
                try {
                    clazz = ClassLoaders.loadClass(annotationClassName);
                }catch (ClassNotFoundException ex){
                    logger.error(ex.getMessage());
                }
                return clazz;
            }
        }).clearNulls().filter(new Predicate<Class>() {
            @Override
            public boolean test(Class clazz) {
                return Reflects.isSubClass(Annotation.class, clazz);
            }
        }).asList();
    }


}
