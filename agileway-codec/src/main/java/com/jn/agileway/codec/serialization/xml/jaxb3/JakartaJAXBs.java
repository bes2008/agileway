package com.jn.agileway.codec.serialization.xml.jaxb3;

import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class JakartaJAXBs {
    private JakartaJAXBs() {
    }

    public static byte[] serialize(Object javaBean) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(javaBean.getClass());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        // 自动换行、缩进
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshal(javaBean, outputStream);
        return outputStream.toByteArray();
    }

    public static <T> T deserialize(String xml, Class<T> expectedClazz) throws Exception {
        if (Strings.isBlank(xml)) {
            return null;
        }
        return deserialize(new ByteArrayInputStream(xml.getBytes(Charsets.UTF_8)), expectedClazz);
    }

    public static <T> T deserialize(InputStream xml, Class<T> expectedClazz) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(expectedClazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (T) unmarshaller.unmarshal(xml);
    }
}
