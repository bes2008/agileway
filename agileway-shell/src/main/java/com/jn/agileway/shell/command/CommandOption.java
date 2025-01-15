package com.jn.agileway.shell.command;

import org.apache.commons.cli.Option;

public class CommandOption extends Option {
    private Object defaultValue;

    public CommandOption(String option, String longOption, boolean hasArg, String description) throws IllegalArgumentException {
        super(option, longOption, hasArg, description);
    }

    public CommandOption cloneOption(Option option){
        CommandOption result = new CommandOption(option.getOpt(), option.getLongOpt(), option.hasArg(), option.getDescription());

        return result;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
