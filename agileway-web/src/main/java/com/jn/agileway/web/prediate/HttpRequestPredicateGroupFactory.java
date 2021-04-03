package com.jn.agileway.web.prediate;

import com.jn.langx.annotation.Name;
import com.jn.langx.factory.Factory;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

public class HttpRequestPredicateGroupFactory implements Factory<HttpRequestPredicateGroupProperties, HttpRequestPredicateGroup> {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestPredicateGroupFactory.class);

    @Override
    public final HttpRequestPredicateGroup get(final HttpRequestPredicateGroupProperties config) {
        final HttpRequestPredicateGroup group = new HttpRequestPredicateGroup();
        if (config == null) {
            return group;
        }
        Collection<Field> fields = Reflects.getAllDeclaredFields(config.getClass(), false);
        Collects.forEach(fields, new Consumer<Field>() {
            @Override
            public void accept(Field field) {
                HttpRequestPredicate predicate = null;
                String fieldName = field.getName();

                HttpRequestPredicateFactory factory = null;
                String alias = null;
                if (Reflects.hasAnnotation(field, Name.class)) {
                    Name n = Reflects.getAnnotation(field, Name.class);
                    alias = n.value();
                }
                if (Objs.isNotEmpty(alias)) {
                    factory = HttpRequestPredicateFactoryRegistry.getInstance().get(alias);
                }
                if (factory == null) {
                    factory = HttpRequestPredicateFactoryRegistry.getInstance().get(fieldName);
                }

                if (factory == null) {
                    logger.warn("Can't find a http-request-predicate factory for {}", fieldName);
                } else {
                    Object value = null;
                    try {
                        value = invokeGetterOrFiled(config, fieldName, true, true);
                    } catch (Throwable ex) {
                        logger.warn("error occur when get http-request-predicate value: {}, error: {},", fieldName, ex.getMessage(), ex);
                    }
                    if (Objs.isNotEmpty(value)) {
                        predicate = factory.get(value);
                    }
                }

                if (predicate != null) {
                    group.add(predicate);
                }
            }
        });
        return group;
    }

    private static <V> V invokeGetterOrFiled(Object object, String field, boolean force, boolean throwException) {
        Preconditions.checkNotNull(object, "the object is null");
        Preconditions.checkNotEmpty(field, "the field name is null or empty");
        Method method = Reflects.getGetter(object.getClass(), field);
        if (method != null) {
            return Reflects.invoke(method, object, new Object[0], force, throwException);
        }
        return Reflects.getAnyFieldValue(object, field, force, throwException);
    }

}
