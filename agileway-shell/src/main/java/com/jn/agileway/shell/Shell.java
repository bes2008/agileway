package com.jn.agileway.shell;

import com.jn.agileway.shell.cmdline.*;
import com.jn.agileway.shell.cmdline.adhoc.AdhocModeCmdlineProvider;
import com.jn.agileway.shell.cmdline.interactive.BannerSupplier;
import com.jn.agileway.shell.cmdline.interactive.InteractiveModeCmdlineProvider;
import com.jn.agileway.shell.cmdline.interactive.PromptSupplier;
import com.jn.agileway.shell.cmdline.script.ScriptModeCmdlineProvider;
import com.jn.agileway.shell.command.*;
import com.jn.agileway.shell.exception.MalformedCommandException;
import com.jn.agileway.shell.exception.NotFoundCommandException;
import com.jn.agileway.shell.exception.ShellInterruptedException;
import com.jn.agileway.shell.result.CmdlineExecResult;
import com.jn.agileway.shell.factory.CompoundCommandComponentFactory;
import com.jn.agileway.shell.result.CmdlineExecResultHandler;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.environment.CompoundEnvironment;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.converter.ConverterService;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Predicate2;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.fusesource.jansi.AnsiConsole;

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
    protected final CmdlineExecutor commandlineExecutor = new DefaultCmdlineExecutor();
    @NonNull
    protected CompoundEnvironment environment;
    protected CmdlineExecResultHandler execResultHandler;
    protected CmdlineProvider cmdlineProvider;
    // 执行结果输出时，是否启用ansi 输出能力
    protected boolean ansiConsoleEnabled;

    /**
     * 当应用命令行是一个命令时，指定命令运行后，以哪种方式处理。该值可以是 AD-HOC 也可以是 INTERACTIVE
     */
    protected RunMode defaultRunMode = RunMode.INTERACTIVE;
    private RunMode runMode;
    private ApplicationArgs appArgs = new ApplicationArgs(new String[0]);
    protected String name;

    // 下面是交互模式下特有的功能：

    /**
     * 命令行提示符
     */
    protected PromptSupplier promptSupplier;

    protected BannerSupplier bannerSupplier;

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

        if (this.defaultRunMode == null || this.defaultRunMode == RunMode.SCRIPT) {
            this.defaultRunMode = RunMode.INTERACTIVE;
        }
        this.runMode = this.autoRegonizeInteractionMode(this.appArgs);

        enableAnsiConsole();

        switch (this.runMode){
            case ADHOC:
                this.cmdlineProvider = new AdhocModeCmdlineProvider(this.appArgs);
                break;
            case INTERACTIVE:
                this.cmdlineProvider = new InteractiveModeCmdlineProvider(this.appArgs, promptSupplier);
                break;
            case SCRIPT:
            default:
                this.cmdlineProvider = new ScriptModeCmdlineProvider(this.appArgs);
                break;
        }
    }

    /**
     * 启用 ansi console，该方法要在 cmdlineProvider 之前调用
     */
    private void enableAnsiConsole() {
        if (ansiConsoleEnabled) {
            String mode = this.environment.getProperty(AnsiConsole.JANSI_MODE, AnsiConsole.JANSI_MODE_FORCE);
            mode = Strings.lowerCase(mode);
            mode = Objs.useValueIfNotMatch(mode, new Predicate<String>() {
                @Override
                public boolean test(String m) {
                    return Lists.newArrayList(AnsiConsole.JANSI_MODE_DEFAULT, AnsiConsole.JANSI_MODE_FORCE, AnsiConsole.JANSI_MODE_STRIP).contains(m);
                }
            }, AnsiConsole.JANSI_MODE_STRIP);
            System.setProperty(AnsiConsole.JANSI_MODE, mode);
            AnsiConsole.systemInstall();
        }
    }

    @Override
    protected void doStart() {
        super.doStart();
        run();
    }

    public void start(String[] appArgs) {
        this.appArgs = new ApplicationArgs(appArgs);
        startup();
    }


    private void run() {
        CmdlineExecResult execResult = null;
        while (execResult==null || !(execResult.getErr() instanceof ShellInterruptedException)) {
            String[] cmdline = null;
            try {
                cmdline = this.cmdlineProvider.get();
            } catch (ShellInterruptedException sie) {
                execResult = new CmdlineExecResult();
                execResult.setErr(sie);
                execResultHandler.handle(execResult);
                continue;
            }
            if (cmdline == null) {
                break;
            }
            if (cmdline.length == 0) {
                if (runMode != RunMode.ADHOC) {
                    continue;
                }
            }
            execResult = evaluate(cmdline);
            execResultHandler.handle(execResult);

            if (runMode == RunMode.ADHOC) {
                break;
            }
        }
    }

    private RunMode autoRegonizeInteractionMode(ApplicationArgs appArgs) {
        if (appArgs.isEmpty()) {
            return this.defaultRunMode;
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
                            return RunMode.SCRIPT;
                        }
                    }
                }
            }

            return this.defaultRunMode;
        } else {
            // 命令中指定了要以 ad-hoc 方式运行时
            if (Collects.contains(cmdline, "--adhoc")) {
                return RunMode.ADHOC;
            } else {
                return this.defaultRunMode;
            }
        }
    }

    private CmdlineExecResult evaluate(String[] cmdline) {
        CmdlineExecResult execResult = new CmdlineExecResult();
        Command commandDef = findCommand(cmdline);
        if (commandDef == null) {
            execResult.setErr(new NotFoundCommandException(Strings.join(" ", cmdline)));
            return execResult;
        }


        Cmdline parsedCmdline = null;

        if(Collects.contains(cmdline, "--help")){
            execResult.setCmdline(null);
            String commandUsage = CommandUtils.commandHelp(commandDef, true);
            execResult.setStdoutData(commandUsage);
        }else {
            try {
                parsedCmdline = this.commandlineParser.parse(commandDef, cmdline);
            } catch (MalformedCommandException e) {
                execResult.setErr(e);
                return execResult;
            }
            execResult = this.commandlineExecutor.exec(parsedCmdline);
            execResult.setCmdline(parsedCmdline);
        }
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
            index = cmdline.length;
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
