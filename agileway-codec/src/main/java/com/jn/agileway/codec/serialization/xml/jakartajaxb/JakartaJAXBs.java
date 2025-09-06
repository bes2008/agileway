package com.jn.agileway.codec.serialization.xml.jakartajaxb;

import com.jn.langx.Factory;
import com.jn.langx.codec.CodecException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.concurrent.threadlocal.ThreadLocalFactory;
import com.jn.langx.util.io.Charsets;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class JakartaJAXBs {
    private JakartaJAXBs() {
    }


    private static final ThreadLocalFactory<Class, Marshaller> marshallerFactory = new ThreadLocalFactory<Class, Marshaller>(new Factory<Class, Marshaller>() {
        @Override
        public Marshaller get(Class clazz) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8");
                // 自动换行、缩进
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                return marshaller;
            } catch (Exception ex) {
                throw new CodecException(ex);
            }
        }
    });

    private static final ThreadLocalFactory<Class, Unmarshaller> unmarshallerFactory = new ThreadLocalFactory<Class, Unmarshaller>(new Factory<Class, Unmarshaller>() {
        @Override
        public Unmarshaller get(Class clazz) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                return unmarshaller;
            } catch (Exception ex) {
                throw new CodecException(ex);
            }
        }
    });


    public static byte[] marshal(Object javaBean) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshal(javaBean, outputStream);
        return outputStream.toByteArray();
    }

    public static void marshal(Object javaBean, OutputStream out) throws CodecException {
        try {
            marshallerFactory.get(javaBean.getClass()).marshal(javaBean, out);
        } catch (Exception ex) {
            throw new CodecException(ex);
        }
    }

    public static <T> T unmarshal(byte[] xml, Class<T> expectedClazz) {
        if (Objs.isEmpty(xml)) {
            return null;
        }
        return unmarshal(new ByteArrayInputStream(xml), expectedClazz);
    }

    public static <T> T unmarshal(String xml, Class<T> expectedClazz) {
        if (Strings.isBlank(xml)) {
            return null;
        }
        return unmarshal(new ByteArrayInputStream(xml.getBytes(Charsets.UTF_8)), expectedClazz);
    }

    public static <T> T unmarshal(InputStream xml, Class<T> expectedClazz) {
        try {
            return (T) unmarshallerFactory.get(expectedClazz).unmarshal(xml);
        } catch (Exception ex) {
            throw new CodecException(ex);
        }
    }
}
