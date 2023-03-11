package com.jn.agileway.protocol.syslog;

import java.util.LinkedHashMap;
import java.util.Map;

public class StructuredData {
    private String id;
    private Map<String, String> structuredDataElements;

    public StructuredData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getStructuredDataElements() {
        return structuredDataElements;
    }

    public void setStructuredDataElements(Map<String, String> structuredDataElements) {
        this.structuredDataElements = structuredDataElements;
    }

    public void addStructuredDataElement(String key, String value) {
        if (this.structuredDataElements == null) {
            this.structuredDataElements = new LinkedHashMap<>();
        }
        this.structuredDataElements.put(key, value);
    }


    /**
     * This instance is equal to all instances of {@code ImmutableStructuredData} that have equal attribute values.
     *
     * @return {@code true} if {@code this} is equal to {@code another} instance
     */
    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof StructuredData
                && equalTo((StructuredData) another);
    }

    private boolean equalTo(StructuredData another) {
        return id.equals(another.id)
                && structuredDataElements.equals(another.structuredDataElements);
    }

    /**
     * Computes a hash code from attributes: {@code id}, {@code structuredDataElements}.
     *
     * @return hashCode value
     */
    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + id.hashCode();
        h += (h << 5) + structuredDataElements.hashCode();
        return h;
    }

    /**
     * Prints the immutable value {@code StructuredData} with attribute values.
     *
     * @return A string representation of the value
     */
    @Override
    public String toString() {
        return "StructuredData{"
                + "id=" + id
                + ", structuredDataElements=" + structuredDataElements
                + "}";
    }

}
