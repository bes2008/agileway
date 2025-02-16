package com.jn.agileway.shell.command;

import com.jn.langx.AbstractNameable;
import com.jn.langx.util.Objs;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Method;

public class CommandAvailability extends AbstractNameable {
    private Method method;
    private String[] commands;

    public CommandAvailability(Method method, String[] commands) {
        this.commands = commands;
        this.method = method;
        this.name = method.getName();
    }

    public Method getMethod() {
        return method;
    }

    public String[] getCommands() {
        return commands;
    }

    public boolean isAvailableFor(Command command) {
        if (commands == null || commands.length == 0) {
            return true;
        }
        for (String cmd : commands) {
            if (Objs.equals(cmd, command.getName())) {
                if(Reflects.isSubClassOrEquals(this.getMethod().getDeclaringClass(), command.getMethod().getDeclaringClass())) {
                    return true;
                }
            }
        }
        return false;
    }
}
