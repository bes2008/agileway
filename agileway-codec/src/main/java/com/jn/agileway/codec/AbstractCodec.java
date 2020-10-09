package com.jn.agileway.codec;

import com.jn.langx.util.reflect.Reflects;

public abstract class AbstractCodec<T> implements Codec<T> {
    protected Class<T> targetType;


    @Override
    public boolean canSerialize(Class type) {
        if (targetType == null) {
            return true;
        }
        return Reflects.isSubClassOrEquals(type, targetType);
    }


    public void setTargetType(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public Class<T> getTargetType() {
        return targetType;
    }
}
