package com.jn.agileway.audit.core.resource.parser.clazz;

import com.jn.agileway.audit.core.model.Resource;
import com.jn.agileway.audit.core.resource.supplier.EntityResourceSupplier;
import com.jn.langx.annotation.Singleton;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.valuegetter.MemberValueGetter;
import org.slf4j.Logger;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

/**
 * 只创建一个实例即可
 *
 * @param <T> the entity class
 * @see DefaultEntityClassResourceSupplierParser
 * @deprecated
 */
@Singleton
@Deprecated
public class MemberNameEntityResourceSupplierParser<T> implements EntityClassResourceSupplierParser<T> {
    private static final Logger logger = Loggers.getLogger(MemberNameEntityResourceSupplierParser.class);
    public static final MemberNameEntityResourceSupplierParser DEFAULT_INSTANCE = new MemberNameEntityResourceSupplierParser();

    @Override
    public EntityResourceSupplier<T> parse(Class<T> entityClass) {
        Map<String, MemberValueGetter> map = parseByFieldName(entityClass);
        if (Emptys.isEmpty(map)) {
            return null;
        }

        EntityResourceSupplier supplier = new EntityResourceSupplier(entityClass);
        supplier.register(map);

        return supplier;
    }

    protected Map<String, MemberValueGetter> parseByFieldName(Class<T> entityClass) {
        Map<String, MemberValueGetter> map = new HashMap<String, MemberValueGetter>();

        parsePropertyByFieldName(entityClass, Resource.RESOURCE_ID_DEFAULT_KEY, map, Resource.RESOURCE_ID);

        parsePropertyByFieldName(entityClass, Resource.RESOURCE_NAME_DEFAULT_KEY, map, Resource.RESOURCE_NAME);

        parsePropertyByFieldName(entityClass, Resource.RESOURCE_TYPE_DEFAULT_KEY, map, Resource.RESOURCE_TYPE);
        return map;
    }

    /**
     * 从entityClass 类中解析 resourceProperty ，解析时使用的字段名称是 fieldName，解析完之后放入map
     */
    protected void parsePropertyByFieldName(Class<T> entityClass, String fieldName, Map<String, MemberValueGetter> map, String resourceProperty) {
        Member member = null;
        try {
            member = Reflects.getGetter(entityClass, fieldName);
            if (member == null) {
                member = Reflects.getAnyField(entityClass, fieldName);
            }
        } catch (Throwable ex) {
            // ignore it
        }
        if (member == null) {
            logger.info("Can't find the resource {} property or getter in the class: {} when parse it using file name ", resourceProperty, entityClass);
        } else {
            map.put(resourceProperty, new MemberValueGetter(member));
        }
    }
}
