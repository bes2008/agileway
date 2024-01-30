package com.jn.agileway.audit.core.resource.supplier;

import com.jn.agileway.audit.core.model.Resource;
import com.jn.agileway.audit.core.resource.ResourceSupplier;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.valuegetter.ValueGetter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity, Map, Iterable, Array 都被视为容器。
 * 每一个容器都会注册多个valueGetter，每一个 valueGetter 是与 Resource 类中的属性一一对应.
 *
 * <pre>
 *     · Entity 容器： 会将字段、方法视为ValueGetter，即VG为 MemberGetter
 *     · Array 容器：VG为： ArrayValueGetter
 *     · Iterable 容器： VG为 IterableValueGetter
 * </pre>
 *
 * @param <Container> Entity, Map, Iterable, Array 都被视为容器。
 * @param <VG>        ValueGetter
 */
public abstract class ContainerResourceSupplier<Container, VG extends ValueGetter> implements ResourceSupplier<Container> {
    /**
     * container 中的 key 到 Resource 的映射
     * <p>
     * key: Resource 中的属性名称
     * value: valueGetter
     */
    private Map<String, VG> propertyToValueGetterMap = new HashMap<String, VG>();

    public void register(String resourceProperty, VG mapValueGetter) {
        propertyToValueGetterMap.put(resourceProperty, mapValueGetter);
    }

    public void register(Map<String, ? extends VG> mapValueGetterMap) {
        propertyToValueGetterMap.putAll(mapValueGetterMap);
    }


    public VG getPropertyValueGetter(String property) {
        return propertyToValueGetterMap.get(property);
    }

    /**
     * 必备的属性
     */
    private static final List<String> requiredResourceProperties = Collects.asList(Resource.RESOURCE_ID, Resource.RESOURCE_NAME);

    /**
     * 获取缺失的 properties
     * @return
     */
    public List<String> getDeficientProperties() {
        List<String> ret = new ArrayList<String>(requiredResourceProperties);
        ret.removeAll(propertyToValueGetterMap.keySet());
        return ret;
    }

    @Override
    public Resource get(final Container container) {
        if (Emptys.isEmpty(container)) {
            return null;
        }

        final Resource resource = new Resource();
        Collects.forEach(propertyToValueGetterMap, new Consumer2<String, VG>() {
            @Override
            public void accept(String resourceProperty, ValueGetter resourcePropertyGetter) {
                // 从entity 获取 值，然后放到 resource对象中
                Field field = Reflects.getAnyField(Resource.class, resourceProperty);
                if (field != null) {
                    Object value = resourcePropertyGetter.get(container);
                    if (value != null) {
                        Reflects.setFieldValue(field, resource, value, true, false);
                    }
                } else {
                    Object value = resourcePropertyGetter.get(container);
                    if (value != null) {
                        resource.put(resourceProperty, value);
                    }
                }
            }
        });


        return resource;
    }
}
