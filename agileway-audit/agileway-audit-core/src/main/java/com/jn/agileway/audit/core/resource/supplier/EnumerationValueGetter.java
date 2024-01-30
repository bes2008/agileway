package com.jn.agileway.audit.core.resource.supplier;

import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.valuegetter.ValueGetter;

public class EnumerationValueGetter implements ValueGetter<Object, Object> {
    private int index;

    public EnumerationValueGetter(int index) {
        this.index = index;
    }

    @Override
    public Object get(Object enumeration) {
        return Pipeline.of(enumeration).asList().get(index);
    }
}
