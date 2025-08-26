package com.jn.agileway.shell.command;

import org.apache.commons.cli.Option;

/**
 * CommandOption 类继承自 Option 类，用于表示命令行选项及其默认值
 * 它允许在命令行工具中定义一个选项，并为其指定一个或多个默认值
 */
public class CommandOption extends Option {
    // 默认值，用于当未在命令行中明确指定该选项的值时使用
    private String defaultValue;
    // 多个默认值，用于支持列表或集合类型的选项默认值
    private String[] defaultValues;

    public CommandOption(String option, String longOption, boolean hasArg, String description) throws IllegalArgumentException {
        super(option, longOption, hasArg, description);
    }

    public CommandOption cloneOption(Option option){
        CommandOption result = new CommandOption(option.getOpt(), option.getLongOpt(), option.hasArg(), option.getDescription());

        return result;
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
}
