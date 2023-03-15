package com.jn.agileway.protocol.syslog;

import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class StructuredElement {
    private String id;
    private Map<String, String> parameters;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        Map<String, String> p = Collects.emptyHashMap(true);
        p.putAll(parameters);
        this.parameters = p;
    }

    public void addParameter(String key, String value) {
        if (this.parameters == null) {
            this.parameters = new LinkedHashMap<>();
        }
        this.parameters.put(key, value);
    }

    @Override
    public String toString() {
        return "StructuredElement{" +
                "id='" + id + '\'' +
                ", parameters=" + parameters +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StructuredElement that = (StructuredElement) o;
        if (!Objs.equals(id, that.id)) {
            return false;
        }
        if (!Objs.equals(parameters, that.parameters)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parameters);
    }
}
