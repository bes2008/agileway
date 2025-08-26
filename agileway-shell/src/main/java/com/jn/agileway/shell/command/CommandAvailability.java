package com.jn.agileway.shell.command;

import com.jn.langx.AbstractNameable;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.pattern.glob.GlobMatcher;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Method;
import java.util.List;

public class CommandAvailability extends AbstractNameable {
    @NonNull
    private Method method;
    @Nullable
    private String[] commands;
    @NonNull
    private List<GlobMatcher> globMatchers;
    public CommandAvailability(Method method, String[] commands) {
        this.method = method;
        this.name = method.getName();
        this.commands = commands;
        List<GlobMatcher> matchers = Lists.newArrayList();
        if(Objs.isNotEmpty(commands)){
            for (String command : commands) {
                GlobMatcher globMatcher = new GlobMatcher();
                globMatcher.setPatternExpression(command);
                matchers.add(globMatcher);
            }
        }
        this.globMatchers = matchers;
    }

    public Method getMethod() {
        return method;
    }

    public String[] getCommands() {
        return commands;
    }

    public boolean isAvailableFor(Command command) {
        if(!Reflects.isSubClassOrEquals(this.getMethod().getDeclaringClass(), command.getMethod().getDeclaringClass())) {
            return false;
        }
        if (globMatchers.isEmpty()) {
            return true;
        }
        for (GlobMatcher matcher : this.globMatchers) {
            if (Boolean.TRUE.equals(matcher.matches(command.getName()))) {
                return true;
            }
        }
        return false;
    }
}
