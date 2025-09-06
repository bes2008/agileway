package com.jn.agileway.codec.serialization.xml.jakartajaxb;

import com.jn.langx.util.Strings;
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

    public static byte[] marshal(Object javaBean) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshal(javaBean, outputStream);
        return outputStream.toByteArray();
    }

    public static void marshal(Object javaBean, OutputStream out) throws Exception {
        javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance(javaBean.getClass());
        javax.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8");
        // 自动换行、缩进
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        marshaller.marshal(javaBean, out);
    }

    public static <T> T unmarshal(String xml, Class<T> expectedClazz) throws Exception {
        if (Strings.isBlank(xml)) {
            return null;
        }
        return unmarshal(new ByteArrayInputStream(xml.getBytes(Charsets.UTF_8)), expectedClazz);
    }

    public static <T> T unmarshal(InputStream xml, Class<T> expectedClazz) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(expectedClazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (T) unmarshaller.unmarshal(xml);
    }
}
