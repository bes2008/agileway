package com.jn.agileway.audit.core.model;

import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.MapAccessor;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于在配置文件中定义资源的标识：
 * 如果一个方法的参数实际就是一个或多个资源，那么可以使用它来标注该参数名称。
 * <p>
 * 也可以这么理解：一个方法的参数名称，如果与 resource的值一样，那么该参数就会被认为是 resource
 * 此时会根据该参数的类型进行区别处理：
 * <pre>
 *     1. 如果是 Map,那么 resourceId, resourceName, resourceType 的值对应的是 map中的key
 *     2. 如果是 Entity，那么  resourceId, resourceName, resourceType 的值对应的是 Entity的字段名称
 *     3. 如果是 List或者数组等，那么resourceId, resourceName, resourceType 的值对应的是 集合的索引
 * </pre>
 * <p>
 * 该值指定的数据的类，需要有 @ResourceMapping注解，或者需要 resourceId, resourceName, resourceType 配置
 */
public class ResourceDefinition extends HashMap<String, Object> {
    public static final long serialVersionUID = 1L;
    private transient MapAccessor accessor;
    /**
     * 是否启用注解方式，默认是true
     */
    private static final String ANNOTATION_ENABLED = "annotation_enabled";
    /**
     * 注解方式是否优先，默认是 false
     */
    private static final String ANNOTATION_FIRST = "annotation_first";

    public static final ResourceDefinition DEFAULT_DEFINITION = getDefaultResourceDefinition();

    private static final ResourceDefinition getDefaultResourceDefinition() {
        return new ResourceDefinition(Collects.<String, Object>emptyHashMap());
    }

    public ResourceDefinition() {
        accessor = new MapAccessor(this);
    }

    public ResourceDefinition(Map<String, Object> map) {
        this();
        this.putAll(map);
        MapAccessor accessor = new MapAccessor(map);
        setResourceId(accessor.getString(Resource.RESOURCE_ID, Resource.RESOURCE_ID_DEFAULT_KEY));
        setResourceName(accessor.getString(Resource.RESOURCE_NAME, Resource.RESOURCE_NAME_DEFAULT_KEY));
        setResourceType(accessor.getString(Resource.RESOURCE_TYPE, Resource.RESOURCE_TYPE_DEFAULT_KEY));
        setResource(accessor.getString("resource"));
        setAnnotationEnabled(accessor.getBoolean(ANNOTATION_ENABLED, true));
        setAnnotationFirst(accessor.getBoolean(ANNOTATION_FIRST, false));
    }

    public void setAnnotationFirst(boolean enabled) {
        accessor.setBoolean(ANNOTATION_FIRST, enabled);
    }

    public boolean isAnnotationFirst() {
        return accessor.getBoolean(ANNOTATION_FIRST, false);
    }

    public void setAnnotationEnabled(boolean enabled) {
        accessor.setBoolean(ANNOTATION_ENABLED, enabled);
    }

    public boolean isAnnotationEnabled() {
        return accessor.getBoolean(ANNOTATION_ENABLED, true);
    }

    public String getResource() {
        return accessor.getString("resource");
    }

    public void setResource(String resource) {
        accessor.set("resource", resource);
    }

    public String getResourceId() {
        return accessor.getString(Resource.RESOURCE_ID);
    }

    public void setResourceId(String resourceId) {
        accessor.set(Resource.RESOURCE_ID, resourceId);
    }

    public String getResourceName() {
        return accessor.getString(Resource.RESOURCE_NAME);
    }

    public void setResourceName(String resourceName) {
        accessor.set(Resource.RESOURCE_NAME, resourceName);
    }

    public String getResourceType() {
        return accessor.getString(Resource.RESOURCE_TYPE);
    }

    public void setResourceType(String resourceType) {
        accessor.set(Resource.RESOURCE_TYPE, resourceType);
    }

    @Override
    public String toString() {
        return JSONs.toJson(this);
    }

    public void setEntityLoader(String entityLoaderKey) {
        accessor.set(Resource.RESOURCE_LOADER, entityLoaderKey);
    }

    public String getEntityLoader(){
        return accessor.getString(Resource.RESOURCE_LOADER);
    }

    public MapAccessor getDefinitionAccessor() {
        return accessor;
    }
}
