package com.jn.agileway.audit.core.resource;

import com.jn.agileway.audit.core.model.Resource;
import com.jn.langx.util.valuegetter.ValueGetter;

/**
 * create a {@link com.jn.agileway.audit.core.model.Resource} object from a entity, collection or other struct
 *
 * @param <T> the entity
 */
public interface ResourceSupplier<T> extends ValueGetter<T, Resource> {
}
