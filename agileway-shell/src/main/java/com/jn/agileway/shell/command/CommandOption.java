package com.jn.agileway.shell.command;

import org.apache.commons.cli.Option;

public class CommandOption extends Option {
    private String defaultValue;
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
