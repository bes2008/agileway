package com.jn.agileway.codec;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.codec.CodecException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.reflect.Reflects;

public abstract class AbstractCodec<T> implements Codec<T> {
    /**
     * 当指定了 target type， 则序列化之前，进行类型判断，反序列后进行类型验证 。
     * 它不能是 Object.class
     * 若不知道，则为通用的 序列化工具。
     */
    private Class<T> expectedTargetType;

    @Override
    public final byte[] encode(T t) throws CodecException {
        if (t == null) {
            return Emptys.EMPTY_BYTES;
        }
        if (canSerialize(t.getClass())) {
            return doEncode(t, isCommonCodec());
        }
        throw new CodecException(StringTemplates.formatWithPlaceholder("can't serialize it, expected type: {}, actual type: {}", expectedTargetType, t.getClass()));
    }

    /**
     * 序列化
     *
     * @param withSchema 是否写 schema， 对于本就支持 写 schema 的 序列化框架，该属性无效
     */
    protected abstract byte[] doEncode(T t, boolean withSchema) throws CodecException;

    @Override
    public final T decode(@Nullable byte[] bytes) throws CodecException {
        return decode(bytes, expectedTargetType);
    }

    @Override
    public final T decode(@Nullable byte[] bytes, @NonNull Class<T> targetType) throws CodecException {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        if (canSerialize(targetType)) {
            T t = doDecode(bytes, isCommonCodec(), targetType);
            if (!isCommonCodec()) {
                if (t != null && targetType != null && !Reflects.isInstance(t, targetType)) {
                    throw new CodecException("error target type: {}" + Reflects.getFQNClassName(targetType));
                }
            }
            return t;
        }
        throw new CodecException("can't deserialize to " + Reflects.getFQNClassName(targetType));
    }

    /**
     * @param withSchema bytes 中是否有 schema 信息，该属性对于本就会写 schema的序列化工具是无效的
     * @param targetType 期望 的序列化类型
     */
    protected abstract T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException;

    @Override
    public final boolean canSerialize(Class type) {
        if (type == null) {
            return true;
        }
        if (!isCommonCodec()) {
            if (!Reflects.isSubClassOrEquals(expectedTargetType, type)) {
                return false;
            }
        }
        return canSerializeIt(type);
    }

    protected boolean canSerializeIt(Class type) {
        return true;
    }


    public void setTargetType(Class<T> expectedTargetType) {
        if (expectedTargetType != Object.class) {
            this.expectedTargetType = expectedTargetType;
        }
    }

    @Override
    public Class<T> getTargetType() {
        return this.expectedTargetType;
    }

    protected boolean isCommonCodec() {
        return this.expectedTargetType == null;
    }
}
