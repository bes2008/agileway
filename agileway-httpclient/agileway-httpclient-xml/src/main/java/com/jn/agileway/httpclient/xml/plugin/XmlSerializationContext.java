package com.jn.agileway.httpclient.xml.plugin;

import com.jn.agileway.httpclient.xml.InternalXmlHttpRequestWriter;
import com.jn.agileway.httpclient.xml.InternalXmlHttpResponseReader;
import com.jn.agileway.httpclient.xml.XmlSerializationConstants;
import com.jn.langx.annotation.Singleton;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.spi.CommonServiceProvider;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
class XmlSerializationContext {
    private static final Logger LOGGER = Loggers.getLogger(XmlSerializationContext.class);

    private Map<Class<?>, String> beanClassUsedXmlFrameworkMap = new ConcurrentHashMap<>();

    private static Map<String, InternalXmlHttpRequestWriter> writerMap = new ConcurrentHashMap<>();
    private static Map<String, InternalXmlHttpResponseReader> readerMap = new ConcurrentHashMap<>();

    private XmlSerializationContext() {

    }

    private static XmlSerializationContext INSTANCE = new XmlSerializationContext();

    public static XmlSerializationContext getInstance() {
        return XmlSerializationContext.INSTANCE;
    }

    static {
        for (InternalXmlHttpResponseReader reader : CommonServiceProvider.loadService(InternalXmlHttpResponseReader.class)) {
            readerMap.put(reader.getName(), reader);
        }
        for (InternalXmlHttpRequestWriter writer : CommonServiceProvider.loadService(InternalXmlHttpRequestWriter.class)) {
            writerMap.put(writer.getName(), writer);
        }
        if (writerMap.isEmpty()) {
            LOGGER.warn("No application/xml http request payload writer found");
        }
        if (readerMap.isEmpty()) {
            LOGGER.warn("No application/xml http request payload reader found");
        }

        if (!readerMap.containsKey(XmlSerializationConstants.JAKARTA_JAXB) && !writerMap.containsKey(XmlSerializationConstants.JAKARTA_JAXB)) {
            LOGGER.warn("No jakarta jaxb xml framework or its implementation found in the classpath, package url: {}", XmlSerializationConstants.JAKARTA_JAXB_PACKAGE_URL);
        }

        if (!readerMap.containsKey(XmlSerializationConstants.JAVAX_JAXB) && !writerMap.containsKey(XmlSerializationConstants.JAVAX_JAXB)) {
            LOGGER.warn("No javax jaxb xml framework or its implementation found in the classpath, package url: {}", XmlSerializationConstants.JAVAX_JAXB_PACKAGE_URL);
        }

        if (!readerMap.containsKey(XmlSerializationConstants.SIMPLE_XML) && !writerMap.containsKey(XmlSerializationConstants.SIMPLE_XML)) {
            LOGGER.warn("No simpleframework xml framework found in the classpath, package url: {}", XmlSerializationConstants.SIMPLE_XML_PACKAGE_URL);
        }

        if (!readerMap.containsKey(XmlSerializationConstants.XSTREAM) && !writerMap.containsKey(XmlSerializationConstants.XSTREAM)) {
            LOGGER.warn("No simpleframework xml framework found in the classpath, package url: {}", XmlSerializationConstants.XSTREAM_PACKAGE_URL);
        }

    }
}

