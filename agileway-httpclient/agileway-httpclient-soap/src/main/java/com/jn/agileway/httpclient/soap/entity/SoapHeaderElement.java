package com.jn.agileway.httpclient.soap.entity;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Strings;

import javax.xml.namespace.QName;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * SOAP Envelope 中可以有多个 SOAP Header
 */
public class SoapHeaderElement {
    @NonNull
    private QName name;

    /**
     * 用于指定由服务端的哪个node来处理SOAP请求
     * <p>
     * 在 SOAP 1.2 中，SOAP Header 的接收者被定义为SOAP 1.2中的role属性
     * </p>
     */
    private URI role;

    /**
     * 是否必须被理解
     */
    private boolean mustUnderstand = false;

    /**
     * 获取或设置一个值，该值指示当前节点不理解 SOAP 标头时是否将该标头中转到下一个 SOAP 节点。
     * SOAP 1.2 规范定义的。
     */
    private boolean relay = false;


    private final Map<String, String> propertySet = new HashMap<String, String>();

    public SoapHeaderElement() {

    }

    public SoapHeaderElement(QName name) {
        this(name, null, false);
    }

    public SoapHeaderElement(QName name, URI role, boolean mustUnderstand) {
        this(name, role, mustUnderstand, false);
    }

    public SoapHeaderElement(QName name, URI role, boolean mustUnderstand, boolean relay) {
        this(name, role, mustUnderstand, relay, null);
    }

    public SoapHeaderElement(QName name, URI role, boolean mustUnderstand, boolean relay, Map<String, String> propertySet) {
        setName(name);
        setRole(role);
        setMustUnderstand(mustUnderstand);
        setRelay(relay);
        setPropertySet(propertySet);
    }


    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public URI getRole() {
        return role;
    }

    public void setRole(URI role) {
        this.role = role;
    }

    public boolean isMustUnderstand() {
        return mustUnderstand;
    }

    public void setMustUnderstand(boolean mustUnderstand) {
        this.mustUnderstand = mustUnderstand;
    }

    public boolean isRelay() {
        return relay;
    }

    public void setRelay(boolean relay) {
        this.relay = relay;
    }

    public boolean isValid() {
        return name != null && Strings.isNotBlank(name.getLocalPart());
    }

    public Map<String, String> getPropertySet() {
        return propertySet;
    }

    public void setPropertySet(Map<String, String> propertySet) {
        this.propertySet.clear();
        if (propertySet != null) {
            this.propertySet.putAll(propertySet);
        }
    }
    public void addProperty(String name, String value) {
        propertySet.put(name, value);
    }
}
