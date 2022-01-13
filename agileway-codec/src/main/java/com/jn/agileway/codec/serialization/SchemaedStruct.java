package com.jn.agileway.codec.serialization;

/**
 * name is the class
 */
public class SchemaedStruct {
    private String name; // class name
    private byte[] value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
}
