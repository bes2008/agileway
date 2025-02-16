package com.jn.agileway.shell.command;

import com.jn.langx.AbstractNameable;
import com.jn.langx.util.Objs;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Method;

public class CommandAvailability extends AbstractNameable {
    private Method method;
    private String[] commands;

    public void setCommands(String[] commands) {
        this.commands = commands;
    }

    public void setMethod(Method method) {
        this.method = method;
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
