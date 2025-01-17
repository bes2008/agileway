package com.jn.agileway.shell.factory;

import com.jn.agileway.shell.command.annotation.Command;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.reflect.Reflects;

import java.util.Map;

public class CustomizedCommandInvokerFactory implements CommandInvokerFactory{
    private Map<Class, Object> invokers = Maps.<Class, Object>newHashMap();

    public void addInvoker(Object invoker){
        if(Reflects.hasAnnotation(invoker.getClass(), Command.class)) {
            invokers.put(invoker.getClass(), invoker);
        }
    }

    @Override
    public Object get(Class type) {
        return invokers.get(type);
    }
}
