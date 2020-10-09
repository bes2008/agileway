package com.jn.agileway.serialization.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jn.agileway.serialization.GenericCodec;
import com.jn.agileway.serialization.CodecException;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;

public class JacksonGenericCodec<T> implements GenericCodec<T> {
    private final ObjectMapper mapper;

    public JacksonGenericCodec() {
        this((String) null);
    }

    public JacksonGenericCodec(@Nullable String classPropertyTypeName) {
        this(new ObjectMapper());
        if (Strings.isNotBlank(classPropertyTypeName)) {
            this.mapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, classPropertyTypeName);
        } else {
            this.mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        }

    }

    public JacksonGenericCodec(ObjectMapper mapper) {
        Preconditions.checkNotNull(mapper, "ObjectMapper must not be null!");
        this.mapper = mapper;
    }


    public byte[] serialize(@Nullable T source) throws CodecException {
        if (source == null) {
            return null;
        } else {
            try {
                return this.mapper.writeValueAsBytes(source);
            } catch (JsonProcessingException ex) {
                throw new CodecException("Could not write JSON: " + ex.getMessage(), ex);
            }
        }
    }

    public T deserialize(@Nullable byte[] source) throws CodecException {
        return (T) this.deserialize(source, (Class<T>) Object.class);
    }

    @Nullable
    public T deserialize(@Nullable byte[] source, Class<T> type) throws CodecException {
        Preconditions.checkNotNull(type, "Deserialization type must not be null! Please provide Object.class to make use of Jackson2 default typing.");
        if (Emptys.isEmpty(source)) {
            return null;
        } else {
            try {
                return this.mapper.readValue(source, type);
            } catch (Exception ex) {
                throw new CodecException("Could not read JSON: " + ex.getMessage(), ex);
            }
        }
    }


    @Override
    public boolean canSerialize(Class<?> type) {
        return true;
    }

    @Override
    public Class<?> getTargetType() {
        return Object.class;
    }
}
