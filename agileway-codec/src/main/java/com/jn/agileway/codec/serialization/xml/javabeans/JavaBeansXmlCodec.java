package com.jn.agileway.codec.serialization.xml.javabeans;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.CodecException;
import com.jn.langx.util.reflect.type.Primitives;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Serializer implementation that uses the JavaBeans
 * {@link java.beans.XMLEncoder XMLEncoder} and {@link java.beans.XMLDecoder XMLDecoder} to serialize
 * and deserialize, respectively.
 * <p/>
 * <b>NOTE:</b> The JavaBeans XMLEncoder/XMLDecoder only successfully encode/decode objects when they are
 * JavaBeans compatible!
 *
 * @since 2.4.1
 * This class should not be used directly because of unsecure XMLEncoder/XMLDecoder usage.
 */

public class JavaBeansXmlCodec<T> extends AbstractCodec<T> {

    @Override
    public byte[] encode(T obj) throws CodecException {
        if (obj == null) {
            String msg = "argument cannot be null.";
            throw new IllegalArgumentException(msg);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(bos));
        encoder.writeObject(obj);
        encoder.close();

        return bos.toByteArray();
    }

    @Override
    public T decode(byte[] bytes) throws CodecException {
        if (bytes == null) {
            throw new IllegalArgumentException("Argument cannot be null.");
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(bis));
        T o = (T) decoder.readObject();
        decoder.close();
        return o;
    }

    @Override
    public T decode(byte[] bytes, Class<T> targetType) throws CodecException {
        T x = decode(bytes);
        if (targetType != null && targetType != Object.class && !Primitives.isPrimitiveOrPrimitiveWrapperType(targetType)) {
            if (x.getClass() == targetType) {
                return x;
            }

        }
        return null;

    }
}
