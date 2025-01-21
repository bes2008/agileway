package com.jn.agileway.shell.command;

import com.jn.agileway.shell.result.CmdOutputTransformer;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.Lists;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.lang.reflect.Method;
import java.util.List;

public class Command {
    private String group;

    private String name;

    private List<String> alias;

    private List<String> optionKeys= Lists.newArrayList();
    private Options options;

    private String desc;

    private Method method;

    @Nullable
    private CmdOutputTransformer outputTransformer;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CmdOutputTransformer getOutputTransformer() {
        return outputTransformer;
    }

    public void setOutputTransformer(CmdOutputTransformer outputTransformer) {
        this.outputTransformer = outputTransformer;
    }

    public void setOptions(List<Option> optionList) {
        Options options = new Options();
        for (Option option : optionList){
            optionKeys.add(option.getKey());
            options.addOption(option);
        }
        this.options = options;
    }

    public List<String> getOptionKeys() {
        return optionKeys;
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

    @Override
    public String toString() {
        return StringTemplates.formatWithPlaceholder( "name: {}, group: {}, alias: {}, desc: {}, options: {}", name, group, alias, desc, options);
    }

    public List<Command> newCommandsForAlias(){
        List<Command> commands = Lists.newArrayList();
        for(String alias : alias) {
            Command command = new Command();
            command.setName(alias);
            command.setDesc(this.desc);
            command.setGroup(this.group);
            command.setMethod(this.method);
            command.optionKeys= this.optionKeys;
            command.options = options;
            commands.add(command);
        }
        return commands;
    }
}
