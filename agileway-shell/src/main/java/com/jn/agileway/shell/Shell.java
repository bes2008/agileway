package com.jn.agileway.shell;

import com.jn.agileway.shell.command.*;
import com.jn.agileway.shell.exception.MalformedCommandException;
import com.jn.agileway.shell.exception.NotFoundCommandException;
import com.jn.agileway.shell.cmdline.CmdExecContext;
import com.jn.agileway.shell.cmdline.DefaultCommandLineExecutor;
import com.jn.agileway.shell.cmdline.CmdlineParser;
import com.jn.agileway.shell.result.CmdExecResult;
import com.jn.agileway.shell.cmdline.Cmdline;
import com.jn.agileway.shell.cmdline.CommandLineExecutor;
import com.jn.agileway.shell.factory.CompoundCommandComponentFactory;
import com.jn.agileway.shell.result.CmdExecResultHandler;
import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.environment.Environment;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Booleans;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.converter.ConverterService;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.logging.Loggers;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
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
    protected CmdlineParser commandlineParser;


    /**
     * 命令执行相关
     */
    @NonNull
    protected CompoundCommandComponentFactory commandComponentFactory;
    protected final CommandLineExecutor commandlineExecutor = new DefaultCommandLineExecutor();
    @NonNull
    protected Environment environment;
    protected CmdExecResultHandler execResultHandler = new CmdExecResultHandler();

    private boolean debugModeEnabled;

    /**
     * 当应用命令行是一个命令时，指定命令运行后，以哪种方式处理。该值可以是 AD-HOC 也可以是 INTERACTIVE
     */
    protected InteractionMode defaultInteractionMode = InteractionMode.INTERACTIVE;
    private InteractionMode interactionMode;
    private ApplicationArgs appArgs = new ApplicationArgs(new String[0]);

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
        this.commandlineExecutor.setCmdExecContext(cmdExecContext);

        this.debugModeEnabled = Booleans.truth(environment.getProperty("agileway.shell.debug.enabled", "false"));
        if (this.defaultInteractionMode == null || this.defaultInteractionMode == InteractionMode.SCRIPT) {
            this.defaultInteractionMode = InteractionMode.INTERACTIVE;
        }
        this.interactionMode = this.autoRegonizeInteractionMode(this.appArgs);
    }

    @Override
    protected void doStart() {
        super.doStart();
    }

    public void start(String[] appArgs) {
        this.appArgs = new ApplicationArgs(appArgs);
        startup();
        run(appArgs);
    }

    private void run(String[] cmdline) {
        CmdExecResult execResult = evaluate(cmdline);

        execResultHandler.handle(execResult);
        if (debugModeEnabled) {
            Loggers.getLogger(Shell.class).info("echo command [{}], execute result: \n{}", Strings.join(" ", cmdline), JSONs.toJson(execResult, true, true));
        }
    }

    private InteractionMode autoRegonizeInteractionMode(ApplicationArgs appArgs) {
        if (appArgs.isEmpty()) {
            return this.defaultInteractionMode;
        }

        String[] cmdline = appArgs.getArgs();

        // 先判断是不是个命令
        Command commandDef = findCommand(cmdline);
        if (commandDef == null) {
            // 判断 是否是以脚本方式运行
            CommandLine commandLine = null;
            try {
                commandLine = new DefaultParser().parse(new Options(), cmdline, true);
            } catch (ParseException e) {
                // ignore it
            }
            if (commandLine != null) {
                List<String> nonOptionsArgs = commandLine.getArgList();
                for (String nonOptionArg : nonOptionsArgs) {
                    if (Strings.startsWith(nonOptionArg, "@") && Objs.length(nonOptionArg) > 1) {
                        Resource scriptResource = Resources.loadResource(nonOptionArg);
                        if (scriptResource != null && scriptResource.isReadable()) {
                            return InteractionMode.SCRIPT;
                        }
                    }
                }
            }

            return this.defaultInteractionMode;
        } else {
            // 命令中指定了要以 ad-hoc 方式运行时
            if (Collects.contains(cmdline, "--adhoc")) {
                return InteractionMode.ADHOC;
            } else {
                return this.defaultInteractionMode;
            }
        }
    }

    private CmdExecResult evaluate(String[] cmdline) {
        CmdExecResult execResult = new CmdExecResult();
        Command commandDef = findCommand(cmdline);
        if (commandDef == null) {
            execResult.setErr(new NotFoundCommandException(Strings.join(" ", cmdline)));
            return execResult;
        }
        Cmdline parsedCmdline = null;
        try {
            parsedCmdline = this.commandlineParser.parse(commandDef, cmdline);
        } catch (MalformedCommandException e) {
            execResult.setErr(e);
        }
        execResult = this.commandlineExecutor.exec(parsedCmdline);
        return execResult;
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
