package com.jn.agileway.shell.command;

import org.apache.commons.cli.Converter;

/**
 * CommandArgument 类用于表示命令行参数的信息
 */
public class CommandArgument {
    // 参数的名称
    private String name;
    // 参数是否是必须的
    private boolean required;
    // 参数的描述
    private String desc;
    // 参数是否可以有多个值
    private boolean multipleValue;

    /**
     * <pre>
     * 如果方法参数是数组，则应该是数组的componentType，也就是数组中的元素的数据类型。
     * 如果方法参数不是数组，就是参数类型。
     * </pre>
     */
    private Class type;

    // 参数的转换器，用于将字符串转换为指定类型
    private Converter converter;



    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    /**
     * 只有方法的最后一个参数有默认值
     */
    private String defaultValue;
    private String[] defaultValues;

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
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
