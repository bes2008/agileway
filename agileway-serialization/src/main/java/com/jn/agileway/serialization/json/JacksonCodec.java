package com.jn.agileway.serialization.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jn.agileway.serialization.AbstractCodec;
import com.jn.agileway.serialization.CodecException;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;

public class JacksonCodec<T> extends AbstractCodec<T> {
    private final ObjectMapper mapper;

    public JacksonCodec() {
        this((String) null);
    }

    public JacksonCodec(@Nullable String classPropertyTypeName) {
        this(new ObjectMapper());
        if (Strings.isNotBlank(classPropertyTypeName)) {
            this.mapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, classPropertyTypeName);
        } else {
            this.mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        }

    }

    public JacksonCodec(ObjectMapper mapper) {
        Preconditions.checkNotNull(mapper, "ObjectMapper must not be null!");
        this.mapper = mapper;
    }


    public byte[] encode(@Nullable T source) throws CodecException {
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

    public T decode(@Nullable byte[] source) throws CodecException {
        return (T) this.decode(source, (Class<T>) Object.class);
    }

    @Nullable
    public T decode(@Nullable byte[] source, Class<T> type) throws CodecException {
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

}
