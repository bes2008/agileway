package com.jn.agileway.codec.serialization.jackson;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;

import javax.xml.datatype.XMLGregorianCalendar;

public class BasedJacksonCodec<T> extends AbstractCodec<T> {
    protected ObjectMapper objectMapper;

    public BasedJacksonCodec() {
        this(new ObjectMapper());
    }

    public BasedJacksonCodec(ClassLoader classLoader) {
        this(createObjectMapper(classLoader, new ObjectMapper()));
    }

    public BasedJacksonCodec(ClassLoader classLoader, BasedJacksonCodec codec) {
        this(createObjectMapper(classLoader, codec.objectMapper.copy()));
    }

    public BasedJacksonCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
        init(this.objectMapper);
        initTypeInclusion(this.objectMapper);
    }

    protected static ObjectMapper createObjectMapper(ClassLoader classLoader, ObjectMapper om) {
        TypeFactory tf = TypeFactory.defaultInstance().withClassLoader(classLoader);
        om.setTypeFactory(tf);
        return om;
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
            setterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ThrowableMixIn {

    }

    protected void init(ObjectMapper objectMapper) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setVisibility(objectMapper.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        objectMapper.addMixIn(Throwable.class, ThrowableMixIn.class);
    }

    protected void initTypeInclusion(ObjectMapper mapObjectMapper) {
        TypeResolverBuilder<?> mapTyper = new ObjectMapper.DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping.NON_FINAL) {
            public boolean useForType(JavaType t) {
                switch (_appliesFor) {
                    case NON_CONCRETE_AND_ARRAYS:
                        while (t.isArrayType()) {
                            t = t.getContentType();
                        }
                        // fall through
                    case OBJECT_AND_NON_CONCRETE:
                        return (t.getRawClass() == Object.class) || !t.isConcrete();
                    case NON_FINAL:
                        while (t.isArrayType()) {
                            t = t.getContentType();
                        }
                        // to fix problem with wrong long to int conversion
                        if (t.getRawClass() == Long.class) {
                            return true;
                        }
                        if (t.getRawClass() == XMLGregorianCalendar.class) {
                            return false;
                        }
                        return !t.isFinal(); // includes Object.class
                    default:
                        // case JAVA_LANG_OBJECT:
                        return t.getRawClass() == Object.class;
                }
            }
        };
        mapTyper.init(JsonTypeInfo.Id.CLASS, null);
        mapTyper.inclusion(JsonTypeInfo.As.PROPERTY);
        mapObjectMapper.setDefaultTyping(mapTyper);
    }

    @Override
    public byte[] encode(T obj) throws CodecException {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (Throwable ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public T decode(byte[] bytes) throws CodecException {
        Class t = getTargetType();
        if (t == null) {
            t = (Class<T>) Object.class;
        }
        return (T) this.decode(bytes, t);
    }

    @Override
    public T decode(byte[] bytes, Class<T> targetType) throws CodecException {
        if (Emptys.isEmpty(bytes)) {
            return null;
        } else {
            try {
                targetType = targetType == null ? (Class<T>) Object.class : targetType;
                return this.objectMapper.readValue(bytes, targetType);
            } catch (Exception ex) {
                throw new CodecException(StringTemplates.formatWithPlaceholder("Could not read {}: {}", objectMapper.getFactory().getFormatName(), ex.getMessage()), ex);
            }
        }
    }
}
