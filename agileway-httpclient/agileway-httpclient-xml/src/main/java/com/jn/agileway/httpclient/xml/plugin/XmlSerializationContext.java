package com.jn.agileway.httpclient.xml.plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class XmlSerializationContext {

    private Map<Class<?>, String> beanClassUsedXmlFrameworkMap = new ConcurrentHashMap<>();

    private Map<String, String> beanClassUsedXmlFrameworkNameMap = new ConcurrentHashMap<>();

}
