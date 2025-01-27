package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.annotation.CommandComponent;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.reflect.Reflects;

import java.util.Map;

public class CustomizedCommandComponentFactory implements CommandComponentFactory {
    private Map<Class, Object> components = Maps.<Class, Object>newHashMap();

    public void addComponent(Object component){
        if(Reflects.hasAnnotation(component.getClass(), CommandComponent.class)) {
            components.put(component.getClass(), component);
        }
    }

    @Override
    public final Object get(Class type) {
        return components.get(type);
    }
}
