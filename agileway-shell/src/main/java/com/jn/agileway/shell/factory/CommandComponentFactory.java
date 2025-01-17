package com.jn.agileway.shell.factory;

import com.jn.langx.Factory;

public interface CommandComponentFactory extends Factory<Class, Object> {
    @Override
    Object get(Class type);
}
