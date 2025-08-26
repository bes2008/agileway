package com.jn.agileway.shell.command;

import com.jn.agileway.shell.result.CmdOutputTransformer;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.lang.reflect.Method;
import java.util.List;
/**
 * Command类代表了一个命令的定义，包含了命令的所有相关信息，如命令的组、名称、别名、选项、参数以及描述等
 * 它主要用于在应用程序中构建和解析命令行接口
 */
public class Command {
    // 命令所属的组名
    private String group;

    // 命令的名称
    private String name;

    // 命令的别名列表，方便用户使用不同的名称引用同一个命令
    private List<String> alias;

    // 命令的选项键列表，用于定义命令可以接受的所有选项
    private List<String> optionKeys = Lists.newArrayList();

    // 选项的详细定义，包括每个选项的类型、描述等
    private Options options;

    // 命令的参数列表，定义了命令期望接收的所有参数
    private List<CommandArgument> arguments = Lists.newArrayList();

    // 命令的描述，提供命令功能的简短说明
    private String desc;

    // 与命令关联的Java方法，当命令执行时，这个方法会被调用
    private Method method;

    /**
     * 可用性定义列表
     */
    private final List<CommandAvailability> availabilityList=Lists.<CommandAvailability>newArrayList();

    // 输出转换器，可选的，用于在命令执行后对输出结果进行转换处理
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

    public List<CommandArgument> getArguments() {
        return arguments;
    }

    public void setArguments(List<CommandArgument> arguments) {
        if(arguments!=null){
            Pipeline.of(arguments).clearNulls().addTo(this.arguments);
        }
    }

    @Override
    public String toString() {
        return StringTemplates.formatWithPlaceholder( "name: {}, group: {}, alias: {}, desc: {}, options: {}", name, group, alias, desc, options);
    }

    public void addAvailability(CommandAvailability availability){
        availabilityList.add(availability);
    }

    public List<CommandAvailability> getAvailabilityList() {
        return availabilityList;
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
