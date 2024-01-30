package com.jn.agileway.audit.core.resource.parser.clazz;

import com.jn.agileway.audit.core.resource.supplier.EntityResourceSupplier;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Function2;

import java.util.List;

/**
 * 推荐单例使用
 *
 * @param <T>
 */
public class DefaultEntityClassResourceSupplierParser<T> implements EntityClassResourceSupplierParser<T> {
    private static List<EntityClassResourceSupplierParser> delegates = Collects.newArrayList(AnnotatedEntityResourceSupplierParser.DEFAULT_INSTANCE, MemberNameEntityResourceSupplierParser.DEFAULT_INSTANCE);
    public static final DefaultEntityClassResourceSupplierParser DEFAULT_INSTANCE = new DefaultEntityClassResourceSupplierParser();

    @Override
    public EntityResourceSupplier<T> parse(final Class<T> entityClass) {
        return Collects.firstMap(delegates, new Function2<Integer, EntityClassResourceSupplierParser, EntityResourceSupplier<T>>() {
            @Override
            public EntityResourceSupplier<T> apply(Integer index, EntityClassResourceSupplierParser delegate) {
                return (EntityResourceSupplier<T>) delegate.parse(entityClass);
            }
        });
    }
}
