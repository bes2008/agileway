package com.jn.agileway.shell;

import com.jn.agileway.shell.builtin.BuiltinCommandsComponentFactory;
import com.jn.agileway.shell.cmdline.interactive.BannerSupplier;
import com.jn.agileway.shell.cmdline.interactive.DefaultBannerSupplier;
import com.jn.agileway.shell.cmdline.interactive.DefaultPromptSupplier;
import com.jn.agileway.shell.cmdline.interactive.PromptSupplier;
import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.command.CommandSupplier;
import com.jn.agileway.shell.command.DefaultCommandSupplier;
import com.jn.agileway.shell.exec.*;
import com.jn.agileway.shell.history.HistoryHandler;
import com.jn.agileway.shell.result.CmdlineExecResultHandler;
import com.jn.langx.Builder;
import com.jn.langx.environment.MultiplePropertySetEnvironment;
import com.jn.langx.propertyset.EnvironmentVariablesPropertySource;
import com.jn.langx.propertyset.PropertySet;
import com.jn.langx.propertyset.SystemPropertiesPropertySource;
import com.jn.langx.util.Strings;
import com.jn.langx.util.SystemPropertys;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.converter.ConverterService;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;

import java.io.File;
import java.util.List;

public class ShellBuilder implements Builder<Shell> {

    private String shellName = "agileway-shell";

    /**
     * 环境相关配置
     */
    private final List<PropertySet> propertySets = Lists.<PropertySet>newArrayList();

    /**
     * 命令定义
     */
    private final List<CommandSupplier> commandsSuppliers = Lists.newArrayList(new DefaultCommandSupplier());

    // 运行模式
    private RunMode defaultRunMode = RunMode.INTERACTIVE;
    private PromptSupplier promptSupplier;
    private BannerSupplier bannerSupplier = new DefaultBannerSupplier();
    // 自定义的命令执行器
    private final List<CmdlineExecutor<?>> cmdlineExecutors = Lists.newArrayList();
    // 为默认的命令执行器提供的 component
    private final List<CommandComponentFactory> componentFactoriesForDefaultExecutor = Lists.newArrayList();

    // 是否启用 ANSI 输出
    private boolean ansiConsoleEnabled = true;
    // 命令历史
    private HistoryHandler historyHandler;


    public ShellBuilder propertySet(PropertySet propertySet) {
        if (propertySet != null) {
            propertySets.add(propertySet);
        }
        return this;
    }


    public ShellBuilder commandsSupplier(CommandSupplier commandsSupplier) {
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

    public ShellBuilder bannerSupplier(BannerSupplier bannerSupplier) {
        if (bannerSupplier != null) {
            this.bannerSupplier = bannerSupplier;
        }
        return this;
    }

    public ShellBuilder componentFactoryForDefaultExecutor(CommandComponentFactory factory){
        if(factory != null) {
            componentFactoriesForDefaultExecutor.add(factory);
        }
        return this;
    }

    public final ShellBuilder cmdlineExecutor(CmdlineExecutor<?> executor){
        if(executor!=null && !(executor instanceof DefaultCmdlineExecutor) ){
            this.cmdlineExecutors.add(executor);
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
        MultiplePropertySetEnvironment environment = new MultiplePropertySetEnvironment("agileway-shell", propertySets);

        shell.environment = environment;
        shell.execResultHandler = new CmdlineExecResultHandler();

        shell.defaultRunMode = this.defaultRunMode;
        shell.ansiConsoleEnabled = this.ansiConsoleEnabled;

        shell.name = shellName;
        shell.promptSupplier = this.promptSupplier == null ? new DefaultPromptSupplier(this.shellName) : promptSupplier;
        shell.bannerSupplier = this.bannerSupplier;

        if (historyHandler == null) {
            historyHandler = new HistoryHandler(new File(SystemPropertys.getUserWorkDir() + "/.agileway-shell-history.log"), 1000);
        }
        shell.historyHandler = historyHandler;

        // 添加自定义的 cmdline executors
        shell.commandlineExecutors.addAll(cmdlineExecutors);

        // 添加默认 executor
        final List<CommandComponentFactory> componentFactories = Lists.newArrayList();
        Pipeline.of(componentFactoriesForDefaultExecutor).forEach(new Predicate<CommandComponentFactory>() {
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
        builtinCommandsComponentFactory.setHistoryHandler(historyHandler);
        componentFactories.add(builtinCommandsComponentFactory);
        componentFactories.add(new ReflectiveCommandComponentFactory());
        CompoundCommandComponentFactory commandComponentFactory = new CompoundCommandComponentFactory(componentFactories);

        DefaultCmdlineExecutor defaultCmdlineExecutor = new DefaultCmdlineExecutor(true);
        defaultCmdlineExecutor.setConverterService(new ConverterService());
        defaultCmdlineExecutor.setEnv(environment);
        defaultCmdlineExecutor.setComponentFactory(commandComponentFactory);
        shell.commandlineExecutors.add(defaultCmdlineExecutor);


        return shell;
    }

}
