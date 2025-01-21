package com.jn.agileway.shell.command;


import com.jn.agileway.shell.result.CmdOutputTransformer;
import com.jn.langx.annotation.Nullable;

public class CommandGroup {
    public static final String BUILTIN_GROUP ="builtin";
    public static final String BUILTIN_GROUP_DESC ="agileway shell builtin commands";
    private String name;
    private String desc;
    @Nullable
    private CmdOutputTransformer outputTransformer;
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

    public CmdOutputTransformer getOutputTransformer() {
        return outputTransformer;
    }

    public void setOutputTransformer(CmdOutputTransformer outputTransformer) {
        this.outputTransformer = outputTransformer;
    }
}
