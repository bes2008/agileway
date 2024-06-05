package com.jn.agileway.codec.serialization.protostuff;

import com.jn.agileway.codec.serialization.SchemaedStruct;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.codec.CodecException;
import com.jn.langx.util.*;
import com.jn.langx.util.reflect.Reflects;
import io.protostuff.GraphIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;

public class Protostuffs {
    private Protostuffs() {
    }

    private static final ConcurrentHashMap<Class<?>, Schema<?>> schemaMap = new ConcurrentHashMap<Class<?>, Schema<?>>();

    public static <T> byte[] serializeWithSchema(T o) {
        if (o == null) {
            return null;
        }
        // 序列化 对象
        byte[] data = serialize(o);

        // 将 对象进行包装，再次写数据
        SchemaedStruct wrappedStruct = new SchemaedStruct();
        wrappedStruct.setName(Reflects.getFQNClassName(o.getClass()));
        wrappedStruct.setValue(data);
        data = serialize(wrappedStruct);
        return data;
    }

    public static <T> void serializeWithSchema(@Nullable T o, @NonNull OutputStream outputStream) throws IOException {
        if (o == null) {
            return;
        }

        // 序列化 对象
        byte[] data = serialize(o);

        // 将 对象进行包装，再次写数据
        SchemaedStruct wrappedStruct = new SchemaedStruct();
        wrappedStruct.setName(Reflects.getFQNClassName(o.getClass()));
        wrappedStruct.setValue(data);

        LinkedBuffer buffer = LinkedBuffer.allocate();
        Schema<T> schema = getSchema(SchemaedStruct.class);
        GraphIOUtil.writeTo(outputStream, o, schema, buffer);
    }

    public static byte[] serialize(Object o) {
        if (o == null) {
            return null;
        }
        // 序列化 对象
        LinkedBuffer buffer = LinkedBuffer.allocate();
        Class objClass = o.getClass();
        Schema schema = getSchema(objClass);
        return GraphIOUtil.toByteArray(o, schema, buffer);
    }

    public static <T> T deserialize(byte[] bytes, @NonNull Class<T> targetType) {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }

        Schema<T> schema = getSchema(targetType);
        if(schema!=null) {
            T instance = schema.newMessage();
            GraphIOUtil.mergeFrom(bytes, instance, schema);
            return instance;
        }else{
            return null;
        }
    }

    public static <T> T deserializeWithSchema(byte[] bytes) {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        try {
            SchemaedStruct wrappedStruct = deserialize(bytes, SchemaedStruct.class);
            if(wrappedStruct!=null) {
                byte[] data = wrappedStruct.getValue();
                String actualClass = wrappedStruct.getName();
                Class<T> targetType = ClassLoaders.loadClass(actualClass);
                return deserialize(data, targetType);
            }
            return null;
        }catch (Throwable ex){
            throw new CodecException(ex);
        }
    }


    public static <T> Schema<T> getSchema(Class targetClass) {
        Preconditions.checkNotNull(targetClass, "the target class is null");
        Schema schema = schemaMap.get(targetClass);
        if (schema == null) {
            schema = RuntimeSchema.getSchema(targetClass);
            if (schema != null) {
                schemaMap.putIfAbsent(targetClass, schema);
            }
        }
        return schema;
    }

    static {
        // 下面的配置都是全局的, 可以参见 io.protostuff.runtime.RuntimeEnv

        // 在序列化时，对于enum ，是否采用 name()，默认为false
        SystemPropertys.setPropertyIfAbsent("protostuff.runtime.enums_by_name", "true");
        // 多态类
        SystemPropertys.setPropertyIfAbsent("protostuff.runtime.auto_load_polymorphic_classes", "true");

        // 数组元素是否允许为 null
        SystemPropertys.setPropertyIfAbsent("protostuff.runtime.allow_null_array_element", "true");

        // 非 final Pojo类的变体，其实就是 是否支持 继承一个Pojo类，默认为 false
        SystemPropertys.setPropertyIfAbsent("protostuff.runtime.morph_non_final_pojos", "true");

        // 是否支持实现 java.util.Collection 接口
        SystemPropertys.setPropertyIfAbsent("protostuff.runtime.morph_collection_interfaces", "true");

        // 是否支持实现 java.util.Map 接口
        SystemPropertys.setPropertyIfAbsent("protostuff.runtime.morph_map_interfaces", "true");


        // 是否序列化集合类中的重复字段，开启后可以处理集合中的元素是其他的依赖
        SystemPropertys.setPropertyIfAbsent("protostuff.runtime.collection_schema_on_repeated_fields", "true");

        // 是否序列化java.util.Collection实现类中的内部字段
        SystemPropertys.setPropertyIfAbsent("protostuff.runtime.pojo_schema_on_collection_fields", "false");
        // 是否序列化java.util.Map实现类中的内部字段
        SystemPropertys.setPropertyIfAbsent("protostuff.runtime.pojo_schema_on_map_fields", "false");

    }
}
