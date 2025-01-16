package com.jn.agileway.shell.command;

import com.jn.langx.util.collection.Lists;
import org.apache.commons.cli.Option;

import java.util.List;

public class CommandOption extends Option {
    private List<Object> defaultValues;

    public CommandOption(String option, String longOption, boolean hasArg, String description) throws IllegalArgumentException {
        super(option, longOption, hasArg, description);
    }

    public CommandOption cloneOption(Option option){
        CommandOption result = new CommandOption(option.getOpt(), option.getLongOpt(), option.hasArg(), option.getDescription());

        return result;
    }


    public void setDefaultValue(Object defaultValue){
        setDefaultValues(Lists.newArrayList(defaultValue));
    }

    public Object getDefaultValue(){
        return defaultValues==null? null: defaultValues.get(0);
    }

    public List<Object> getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(List<Object> defaultValues) {
        this.defaultValues = defaultValues;
    }
}
