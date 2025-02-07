package com.jn.agileway.shell.command;


import com.jn.agileway.shell.result.CmdOutputTransformer;
import com.jn.langx.annotation.Nullable;
/**
 * CommandGroup类代表了一个命令组，用于对相关的命令进行分组和管理
 * 这个类存储了组的名称、描述以及一个可选的输出转换器，用于统一处理命令的输出
 */
public class CommandGroup {
    // 内置命令组的标识符，用于标识一组预定义的命令
    public static final String BUILTIN_GROUP ="builtin";

    // 内置命令组的描述，提供了关于内置命令组的一些基本信息
    public static final String BUILTIN_GROUP_DESC ="The builtin commands in agileway shell";

    // 命令组的名称，用于唯一标识一个命令组
    private String name;

    // 命令组的描述，提供了关于命令组功能的详细信息
    private String desc;

    // 输出转换器，用于处理和转换命令的输出，可能为null，表示没有输出转换器
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
