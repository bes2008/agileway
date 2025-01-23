package com.jn.agileway.shell.command;

import org.apache.commons.cli.Converter;

public class CommandArgument {
    private String name;
    private boolean required;
    private String desc;
    private boolean multipleValue;

    private Class parameterType;

    private Converter converter;

    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    public Class getParameterType() {
        return parameterType;
    }

    /**
     * 只有方法的最后一个参数有默认值
     */
    private String defaultValue;
    private String[] defaultValues;

    public void setParameterType(Class parameterType) {
        this.parameterType = parameterType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String[] getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(String[] defaultValues) {
        this.defaultValues = defaultValues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isMultipleValue() {
        return multipleValue;
    }

    public void setMultipleValue(boolean multipleValue) {
        this.multipleValue = multipleValue;
    }
}
