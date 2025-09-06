package com.jn.agileway.codec.serialization.xml.javabeans;

import com.jn.agileway.codec.serialization.xml.AbstractOXMCodec;
import com.jn.langx.codec.CodecException;

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

public class JavaBeansXmlCodec<T> extends AbstractOXMCodec<T> {

    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(bos));
        encoder.writeObject(t);
        encoder.close();

        return bos.toByteArray();
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(bis));
        T o = (T) decoder.readObject();
        decoder.close();
        return o;
    }


}
