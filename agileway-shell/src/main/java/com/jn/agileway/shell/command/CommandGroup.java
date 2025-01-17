package com.jn.agileway.shell.command;


public class CommandGroup {
    public static final String DEFAULT_GROUP="DEFAULT";
    public static final String DEFAULT_GROUP_DESC="agile shell builtin commands";
    private String name;
    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }



}
