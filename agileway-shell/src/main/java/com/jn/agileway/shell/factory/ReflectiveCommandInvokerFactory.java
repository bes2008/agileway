package com.jn.agileway.shell.factory;

import com.jn.langx.util.reflect.Reflects;

public final class ReflectiveCommandInvokerFactory implements CommandInvokerFactory {
    @Override
    public Object get(Class type) {
        return Reflects.newInstance(type);
    }
}
