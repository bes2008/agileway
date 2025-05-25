package com.jn.agileway.httpclient.soap;

import java.util.ArrayList;
import java.util.List;

public class SoapHeader {
    private String version = "1.2";

    private List<SoapHeaderElement> elements = new ArrayList<SoapHeaderElement>();

    public List<SoapHeaderElement> getElements() {
        return elements;
    }

    public void setElements(List<SoapHeaderElement> elements) {
        if (elements != null) {
            for (SoapHeaderElement element : elements) {
                addElement(element);
            }
        }
    }

    public void addElement(SoapHeaderElement element) {
        if (element != null && element.isValid()) {
            this.elements.add(element);
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
