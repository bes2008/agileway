package com.jn.agileway.shell;

import com.jn.agileway.shell.command.*;
import com.jn.agileway.shell.exception.MalformedCommandException;
import com.jn.agileway.shell.exception.NotFoundCommandException;
import com.jn.agileway.shell.exec.CmdExecContext;
import com.jn.agileway.shell.exec.DefaultCommandLineExecutor;
import com.jn.agileway.shell.result.CmdExecResult;
import com.jn.agileway.shell.exec.Cmdline;
import com.jn.agileway.shell.exec.CommandLineExecutor;
import com.jn.agileway.shell.factory.CompoundCommandComponentFactory;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.environment.Environment;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.converter.ConverterService;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate2;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;

import java.util.List;
import java.util.Map;

public class Shell extends AbstractLifecycle {
    /**
     * 命令定义相关
     */
    @NonNull
    protected CommandRegistry commandRegistry;
    @NonNull
    protected List<CommandsSupplier> commandsSuppliers;

    /**
     * 命令解析
     */
    @NonNull
    protected CommandLineParser commandlineParser;
    /**
     * 解析命令行时，遇到了一个option时，如果选项是未知的，是否停止解析。
     * 如果停止解析，那么后续的字符串都将作为args
     * 如果不停止，就会抛出UnrecognizedOptionException
     */
    protected boolean stopParseAtNonDefinedOption = true;


    /**
     * 命令执行相关
     */
    @NonNull
    protected CompoundCommandComponentFactory commandComponentFactory;
    private CmdExecContext cmdExecContext;
    protected final CommandLineExecutor commandlineExecutor = new DefaultCommandLineExecutor();

    @NonNull
    protected Environment environment;

    private ApplicationArgs args;

    Shell() {

    }

    @Override
    protected void doInit() throws InitializationException {
        Pipeline.of(commandsSuppliers).forEach(new Consumer<CommandsSupplier>() {
            @Override
            public void accept(CommandsSupplier commandsSupplier) {
                Map<CommandGroup, List<Command>> groupCommandsMap = commandsSupplier.get(environment);
                if (groupCommandsMap == null) {
                    return;
                }
                for (Map.Entry<CommandGroup, List<Command>> entry : groupCommandsMap.entrySet()) {
                    CommandGroup commandGroup = entry.getKey();
                    commandRegistry.addCommandGroup(commandGroup);
                    List<Command> commands = entry.getValue();
                    for (Command command : commands) {
                        command.setGroup(commandGroup.getName());
                        commandRegistry.addCommand(command);
                    }
                }
            }
        });

        CmdExecContext cmdExecContext = new CmdExecContext();
        cmdExecContext.setEnv(environment);
        cmdExecContext.setComponentFactory(commandComponentFactory);
        cmdExecContext.setConverterService(new ConverterService());
    }

    @Override
    protected void doStart() {
        super.doStart();
    }

    public void start(String[] args) {
        this.args = new ApplicationArgs(args);
        startup();
        run(this.args.getArgs());
    }

    private void run(String[] cmdlineStrings) {
        Command commandDef = findCommand(cmdlineStrings);
        if (commandDef == null) {
            throw new NotFoundCommandException(Strings.join(" ", cmdlineStrings));
        }
        Cmdline cmdline = null;
        try {
            CommandLine parsedCommandLine = commandlineParser.parse(commandDef.getOptions(), cmdlineStrings, stopParseAtNonDefinedOption);
            cmdline = new Cmdline(commandDef, parsedCommandLine);
        } catch (ParseException e) {
            throw new MalformedCommandException(e);
        }
        CmdExecResult execResult = this.commandlineExecutor.exec(cmdline);
        System.out.println(this.args.getRaw());
    }

    private Command findCommand(String[] cmdline) {
        int index = Collects.<String, List>firstOccurrence(Collects.asList(cmdline), new Predicate2<Integer, String>() {
            @Override
            public boolean test(Integer integer, String s) {
                return Strings.startsWith(s, "-");
            }
        });
        String commandKey = null;
        if (index < 0) {
            commandKey = Strings.join(" ", cmdline);
        } else if (index > 0) {
            commandKey = Strings.join(" ", cmdline, 0, index);
        }
        commandKey = Strings.trimToNull(commandKey);
        Command command = null;
        if (Strings.isNotEmpty(commandKey)) {
            command = commandRegistry.getCommand(commandKey);
            index--;
            while (command == null && index >= 1) {
                String newCommandKey = Strings.join(" ", cmdline, 0, index);
                command = commandRegistry.getCommand(newCommandKey);
                index--;
            }
        }
        return command;
    }

    @Override
    protected void doStop() {
        super.doStop();
    }
}
