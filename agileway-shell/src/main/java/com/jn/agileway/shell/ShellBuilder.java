package com.jn.agileway.shell;

import com.jn.agileway.shell.builtin.BuiltinCommandsComponentFactory;
import com.jn.agileway.shell.cmdline.interactive.DefaultPromptSupplier;
import com.jn.agileway.shell.cmdline.interactive.PromptSupplier;
import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.command.CommandsSupplier;
import com.jn.agileway.shell.command.DefaultCommandsSupplier;
import com.jn.agileway.shell.factory.CommandComponentFactory;
import com.jn.agileway.shell.factory.CompoundCommandComponentFactory;
import com.jn.agileway.shell.factory.ReflectiveCommandComponentFactory;
import com.jn.agileway.shell.cmdline.CmdlineParser;
import com.jn.agileway.shell.result.CmdlineExecResultHandler;
import com.jn.langx.Builder;
import com.jn.langx.environment.MultiplePropertySetEnvironment;
import com.jn.langx.propertyset.EnvironmentVariablesPropertySource;
import com.jn.langx.propertyset.PropertySet;
import com.jn.langx.propertyset.SystemPropertiesPropertySource;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;

import java.util.List;

public class ShellBuilder implements Builder<Shell> {
    private List<CommandComponentFactory> commandComponentFactories = Lists.newArrayList();
    private List<PropertySet> propertySets = Lists.<PropertySet>newArrayList();
    private boolean stopParseAtNonDefinedOption = true;
    private RunMode defaultRunMode = RunMode.INTERACTIVE;
    private final List<CommandsSupplier> commandsSuppliers = Lists.newArrayList(new DefaultCommandsSupplier());
    private boolean ansiConsoleEnabled = true;

    private String shellName = "agileway-shell";

    private PromptSupplier promptSupplier;

    public ShellBuilder componentFactory(CommandComponentFactory factory) {
        if (factory != null) {
            commandComponentFactories.add(factory);
        }
        return this;
    }

    public ShellBuilder propertySet(PropertySet propertySet) {
        if (propertySet != null) {
            propertySets.add(propertySet);
        }
        return this;
    }

    public ShellBuilder stopParseAtNonDefinedOption(boolean stop) {
        this.stopParseAtNonDefinedOption = stop;
        return this;
    }

    public ShellBuilder commandsSupplier(CommandsSupplier commandsSupplier) {
        if (commandsSupplier != null) {
            this.commandsSuppliers.add(commandsSupplier);
        }
        return this;
    }

    public ShellBuilder defaultRunMode(RunMode defaultRunMode) {
        if (defaultRunMode != null) {
            this.defaultRunMode = defaultRunMode;
        }
        return this;
    }

    public ShellBuilder ansiConsoleEnabled(boolean enabled) {
        this.ansiConsoleEnabled = enabled;
        return this;
    }

    public ShellBuilder name(String name) {
        name = Strings.trimToNull(name);
        if (name != null) {
            this.shellName = name;
        }
        return this;
    }

    public ShellBuilder promptSupplier(PromptSupplier promptSupplier) {
        this.promptSupplier = promptSupplier;
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

        shell.execResultHandler = new CmdlineExecResultHandler();

        shell.commandlineParser = new CmdlineParser(this.stopParseAtNonDefinedOption);


        shell.defaultRunMode = this.defaultRunMode;
        shell.ansiConsoleEnabled = this.ansiConsoleEnabled;


        final List<CommandComponentFactory> componentFactories = Lists.newArrayList();
        Pipeline.of(commandComponentFactories).forEach(new Predicate<CommandComponentFactory>() {
            @Override
            public boolean test(CommandComponentFactory factory) {
                return factory instanceof ReflectiveCommandComponentFactory;
            }
        }, new Consumer<CommandComponentFactory>() {
            @Override
            public void accept(CommandComponentFactory factory) {
                componentFactories.add(factory);
            }
        });
        BuiltinCommandsComponentFactory builtinCommandsComponentFactory = new BuiltinCommandsComponentFactory();
        builtinCommandsComponentFactory.setCommandRegistry(shell.commandRegistry);
        builtinCommandsComponentFactory.setCmdExecResultHandler(shell.execResultHandler);
        componentFactories.add(builtinCommandsComponentFactory);
        componentFactories.add(new ReflectiveCommandComponentFactory());
        shell.commandComponentFactory = new CompoundCommandComponentFactory(componentFactories);

        shell.name = shellName;
        shell.promptSupplier = this.promptSupplier == null ? new DefaultPromptSupplier(this.shellName) : promptSupplier;
        return shell;
    }
}
