package com.jn.agileway.shell.exec;

import com.jn.langx.util.reflect.Reflects;

public final class ReflectiveCommandComponentFactory implements CommandComponentFactory {
    @Override
    public Object get(Class type) {
        return Reflects.newInstance(type);
    }
}
