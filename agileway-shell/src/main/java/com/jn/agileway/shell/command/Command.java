package com.jn.agileway.shell.command;

import org.apache.commons.cli.Options;

import java.lang.reflect.Method;
import java.util.List;


public class Command {
    private String group;

    private String name;

    private List<String> alias;

    private Options options;

    private String desc;


    private Method method;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public Options getOptions() {
        return options;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<String> getAlias() {
        return alias;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
