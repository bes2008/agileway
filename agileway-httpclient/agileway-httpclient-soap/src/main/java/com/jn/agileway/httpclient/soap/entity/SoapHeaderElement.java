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

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public URI getActor() {
        return getRole();
    }

    public void setActor(URI actor) {
        this.setRole(actor);
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

    public void addProperty(String name, String value) {
        propertySet.put(name, value);
    }
}
