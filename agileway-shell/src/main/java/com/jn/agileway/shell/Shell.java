package com.jn.agileway.shell;

import com.jn.agileway.shell.command.Command;
import com.jn.agileway.shell.command.CommandGroup;
import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.command.CommandsSupplier;
import com.jn.agileway.shell.exception.NotFoundCommandException;
import com.jn.agileway.shell.factory.CompoundCommandComponentFactory;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.environment.Environment;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate2;
import org.apache.commons.cli.CommandLineParser;

import java.util.List;
import java.util.Map;

public class Shell extends AbstractLifecycle {
    @NonNull
    protected CommandRegistry commandRegistry;
    @NonNull
    protected CompoundCommandComponentFactory componentFactory;
    @NonNull
    protected CommandLineParser commandLineParser;

    @NonNull
    protected List<CommandsSupplier> commandsSuppliers;
    @NonNull
    protected Environment environment;

    private ApplicationArgs args;

    Shell(){

    }

    @Override
    protected void doInit() throws InitializationException {
        Pipeline.of(commandsSuppliers).forEach(new Consumer<CommandsSupplier>() {
            @Override
            public void accept(CommandsSupplier commandsSupplier) {
                Map<CommandGroup, List<Command>> groupCommandsMap = commandsSupplier.get(environment);
                if(groupCommandsMap==null){
                    return;
                }
                for (Map.Entry<CommandGroup, List<Command>> entry: groupCommandsMap.entrySet()){
                    CommandGroup commandGroup = entry.getKey();
                    commandRegistry.addCommandGroup(commandGroup);
                    List<Command> commands = entry.getValue();
                    for (Command command : commands){
                        command.setGroup(commandGroup.getName());
                        commandRegistry.addCommand(command);
                    }
                }
            }
        });
    }

    @Override
    protected void doStart() {
        super.doStart();
    }

    public void start(String[] args){
        this.args = new ApplicationArgs(args);
        startup();
        run(this.args.getArgs());
    }

    private void run(String[] cmdline){
        Command command = findCommand(cmdline);
        if(command==null){
            throw new NotFoundCommandException(Strings.join(" ", cmdline));
        }else {
            System.out.println(command);
        }
        System.out.println(this.args.getRaw());
    }

    private Command findCommand(String[] cmdline){
        int index = Collects.<String,List>firstOccurrence(Collects.asList(cmdline), new Predicate2<Integer, String>() {
            @Override
            public boolean test(Integer integer, String s) {
                return Strings.startsWith(s,"-");
            }
        });
        String commandKey = null;
        if(index<0){
            commandKey = Strings.join(" ", cmdline);
        }else if(index>0){
            commandKey = Strings.join(" ", cmdline, 0, index);
        }
        commandKey = Strings.trimToNull(commandKey);
        Command command = null;
        if(Strings.isNotEmpty(commandKey)){
            command = commandRegistry.getCommand(commandKey);
            index--;
            while(command==null && index>=1){
                String newCommandKey = Strings.join(" ", cmdline, 0,index);
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
