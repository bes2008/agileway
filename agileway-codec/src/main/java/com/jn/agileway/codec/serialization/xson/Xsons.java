package com.jn.agileway.codec.serialization.xson;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.codec.CodecException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.reflect.Reflects;
import org.xson.core.XSON;

import java.io.IOException;
import java.io.OutputStream;

public class Xsons {
    private Xsons() {
    }

    public static <T> byte[] serialize(@Nullable T o) throws IOException {
        if (o == null) {
            return null;
        }
        return XSON.encode(o);
    }

    public static <T> void serialize(@NonNull T o, @NonNull OutputStream outputStream) throws IOException {
        byte[] bytes = serialize(o);
        if (Emptys.isNotEmpty(bytes)) {
            outputStream.write(bytes);
        }
    }

    public static <T> T deserialize(@Nullable byte[] bytes) {
        return XSON.decode(bytes);
    }

    public static <T> T deserialize(@Nullable byte[] bytes, @Nullable Class<T> targetClass) {
        Object obj = deserialize(bytes);
        if (obj != null && targetClass != null) {
            if (!Reflects.isInstance(obj, targetClass)) {
                throw new CodecException(StringTemplates.formatWithPlaceholder("{} is not cast to {} when use XSON deserialize", Reflects.getFQNClassName(obj.getClass()), Reflects.getFQNClassName(targetClass)));
            }
        }
        return (T) obj;
    }

}
