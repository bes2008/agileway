package com.jn.agileway.shell.command.def;

import org.apache.commons.cli.Options;

import java.lang.reflect.Method;


public class Command {
    private String name;

    private Options options;

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

}
