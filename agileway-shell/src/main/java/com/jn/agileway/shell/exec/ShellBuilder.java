package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.factory.CommandInvokerFactory;
import com.jn.langx.Builder;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.environment.Environment;
import com.jn.langx.environment.MultiplePropertySetEnvironment;
import com.jn.langx.propertyset.EnvironmentVariablesPropertySource;
import com.jn.langx.propertyset.PropertySet;
import com.jn.langx.propertyset.SystemPropertiesPropertySource;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.function.Supplier;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;

import java.util.List;

public class ShellBuilder implements Builder<Shell> {
    private List<CommandInvokerFactory> commandInvokerFactories = Lists.newArrayList();
    @Nullable
    private CommandLineParser commandLineParser;

    private List<PropertySet> propertySets = Lists.<PropertySet>newArrayList();

    public ShellBuilder with(CommandInvokerFactory factory){
        if(factory!=null) {
            commandInvokerFactories.add(factory);
        }
        return this;
    }

    public ShellBuilder withCommandLineParser(CommandLineParser parser){
        this.commandLineParser = parser;
        return this;
    }

    public ShellBuilder withPropertySet(PropertySet propertySet){
        if(propertySet!=null){
            propertySets.add(propertySet);
        }
        return this;
    }

    @Override
    public Shell build() {

        Shell shell = new Shell();
        shell.commandLineParser = Objs.useValueIfEmpty(this.commandLineParser, new DefaultParser());
        shell.commandRegistry = new CommandRegistry();

        propertySets.add(new SystemPropertiesPropertySource());
        propertySets.add(new EnvironmentVariablesPropertySource());
        shell.environment = new MultiplePropertySetEnvironment("agileway-shell", propertySets);

        return null;
    }
}
