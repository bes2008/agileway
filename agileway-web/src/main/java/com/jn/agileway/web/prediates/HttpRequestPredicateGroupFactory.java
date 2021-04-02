package com.jn.agileway.web.prediates;

import com.jn.langx.factory.Factory;
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
        Collection<Field> fields = Reflects.getAllDeclaredFields(config.getClass(), false);
        Collects.forEach(fields, new Consumer<Field>() {
            @Override
            public void accept(Field field) {
                HttpRequestPredicate predicate = null;
                String fieldName = field.getName();
                HttpRequestPredicateFactory factory = HttpRequestPredicateFactoryRegistry.getInstance().get(fieldName);

                if (factory == null) {
                    logger.warn("Can't find a http-request-predicate factory for {}", fieldName);
                } else {
                    Object value = invokeGetterOrFiled(config, fieldName, true, true);
                    predicate = factory.get(value);
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
