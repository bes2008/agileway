package com.jn.agileway.shell.exec;

import com.jn.langx.Factory;

public interface CommandComponentFactory extends Factory<Class, Object> {
    @Override
    Object get(Class type);
}
