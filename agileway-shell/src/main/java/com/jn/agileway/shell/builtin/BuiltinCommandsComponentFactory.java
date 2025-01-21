package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.factory.CommandComponentFactory;

public class BuiltinCommandsComponentFactory implements CommandComponentFactory {
    private CommandRegistry commandRegistry;

    public BuiltinCommandsComponentFactory(CommandRegistry commandRegistry){
        this.commandRegistry = commandRegistry;
    }

    @Override
    public Object get(Class type) {
        if(type == UsageCommands.class){
            return new UsageCommands(this.commandRegistry);
        }
        return null;
    }
}
