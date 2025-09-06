package com.jn.agileway.codec.serialization.xml.javaxjaxb;

import com.jn.langx.codec.CodecException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class JavaxJAXBs {
    private JavaxJAXBs() {
    }

    public static byte[] marshal(Object javaBean) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshal(javaBean, outputStream);
        return outputStream.toByteArray();
    }

    public static void marshal(Object javaBean, OutputStream out) throws CodecException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(javaBean.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8");
            // 自动换行、缩进
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(javaBean, out);
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
            JAXBContext jaxbContext = JAXBContext.newInstance(expectedClazz);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (T) unmarshaller.unmarshal(xml);
        } catch (Exception ex) {
            throw new CodecException(ex);
        }
    }
}
