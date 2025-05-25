package com.jn.agileway.httpclient.soap.entity;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Strings;

import javax.xml.namespace.QName;

/**
 * SOAP Envelope 中可以有多个 SOAP Header
 */
public class SoapHeaderElement {
    @NonNull
    private QName name;

    /**
     * SOAP Header 的接收者
     * <p>
     * 在 SOAP 1.2 中，SOAP Header 的接收者被定义为SOAP 1.2中的role属性
     * </p>
     */
    private String role = "";

    /**
     * 是否必须被理解
     */
    private boolean mustUnderstand = false;

    /**
     * 获取或设置一个值，该值指示当前节点不理解 SOAP 标头时是否将该标头中转到下一个 SOAP 节点。
     * SOAP 1.2 规范定义的。
     */
    private boolean relay = false;

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public String getActor() {
        return getRole();
    }

    public void setActor(String actor) {
        this.setRole(actor);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
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
}
