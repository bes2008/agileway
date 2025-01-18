package com.jn.agileway.shell;

import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.command.CommandsSupplier;
import com.jn.agileway.shell.command.DefaultCommandsSupplier;
import com.jn.agileway.shell.exec.CommandLineExecutor;
import com.jn.agileway.shell.exec.DefaultCommandLineExecutor;
import com.jn.agileway.shell.factory.CommandComponentFactory;
import com.jn.agileway.shell.factory.CompoundCommandComponentFactory;
import com.jn.agileway.shell.factory.ReflectiveCommandComponentFactory;
import com.jn.langx.Builder;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.environment.MultiplePropertySetEnvironment;
import com.jn.langx.propertyset.EnvironmentVariablesPropertySource;
import com.jn.langx.propertyset.PropertySet;
import com.jn.langx.propertyset.SystemPropertiesPropertySource;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;

import java.util.List;

public class ShellBuilder implements Builder<Shell> {
    private List<CommandComponentFactory> commandComponentFactories = Lists.newArrayList();
    private CommandLineParser commandlineParser = new DefaultParser();


    private List<PropertySet> propertySets = Lists.<PropertySet>newArrayList();

    private final List<CommandsSupplier> commandsSuppliers = Lists.newArrayList(new DefaultCommandsSupplier());

    public ShellBuilder with(CommandComponentFactory factory){
        if(factory!=null) {
            commandComponentFactories.add(factory);
        }
        return this;
    }

    public ShellBuilder with(CommandLineParser parser){
        if(parser!=null) {
            this.commandlineParser = parser;
        }
        return this;
    }

    public ShellBuilder with(PropertySet propertySet){
        if(propertySet!=null){
            propertySets.add(propertySet);
        }
        return this;
    }

    public ShellBuilder with(CommandLineExecutor commandlineExecutor){
        if(commandlineExecutor!=null){
            this.commandlineExecutor = commandlineExecutor;
        }
        return this;
    }

    public ShellBuilder with(CommandsSupplier commandsSupplier){
        if(commandsSupplier!=null){
            this.commandsSuppliers.add(commandsSupplier);
        }
        return this;
    }

    @Override
    public Shell build() {

        Shell shell = new Shell();

        shell.commandRegistry = new CommandRegistry();
        shell.commandsSuppliers = commandsSuppliers;
        propertySets.add(new SystemPropertiesPropertySource());
        propertySets.add(new EnvironmentVariablesPropertySource());
        shell.environment = new MultiplePropertySetEnvironment("agileway-shell", propertySets);

        shell.commandlineParser = this.commandlineParser;

        final List<CommandComponentFactory> invokerFactories = Lists.newArrayList();
        Pipeline.of(commandComponentFactories).forEach(new Predicate<CommandComponentFactory>() {
            @Override
            public boolean test(CommandComponentFactory factory) {
                return factory instanceof ReflectiveCommandComponentFactory;
            }
        }, new Consumer<CommandComponentFactory>() {
            @Override
            public void accept(CommandComponentFactory factory) {
                invokerFactories.add(factory);
            }
        });
        invokerFactories.add(new ReflectiveCommandComponentFactory());
        shell.componentFactory = new CompoundCommandComponentFactory(invokerFactories);

        shell.commandlineExecutor = new DefaultCommandLineExecutor(shell.environment);

        return shell;
    }
}
