package com.jn.agileway.httpclient.xml;

import com.jn.langx.spec.purl.PackageURLBuilder;

public class XmlSerializationConstants {
    public static final String JAKARTA_JAXB = "JAKARTA_JAXB";
    public static final String JAKARTA_JAXB_PACKAGE_URL = new PackageURLBuilder().withNamespace("jakarta.xml.bind").withName("jakarta.xml.bind-api").withVersion("3.0.x-or-4.0.x").build().toPURLString();
    public static final String JAVAX_JAXB = "JAVAX_JAXB";

    public static final String JAVAX_JAXB_PACKAGE_URL = new PackageURLBuilder().withNamespace("javax.xml.bind").withName("jaxb-api").withVersion("2.x").build().toPURLString();

    public static final String XSTREAM = "XSTREAM";
    public static final String XSTREAM_PACKAGE_URL = new PackageURLBuilder().withNamespace("com.thoughtworks.xstream").withName("xstream").withVersion("1.x").build().toPURLString();

    public static final String SIMPLE_XML = "SIMPLE_XML";
    public static final String SIMPLE_XML_PACKAGE_URL = new PackageURLBuilder().withNamespace("org.simpleframework").withName("simple-xml").withVersion("2.x").build().toPURLString();
}
