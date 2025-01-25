package com.jn.agileway.shell.exec;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.struct.Holder;

import java.util.List;

public final class CompoundCommandComponentFactory implements CommandComponentFactory{
    private List<CommandComponentFactory> delegates;

    public CompoundCommandComponentFactory(List<CommandComponentFactory> delegates){
        this.delegates = delegates;
    }
    @Override
    public Object get(final Class type) {
        final Holder<Object> componentBeanHolder = new Holder<>();
        Collects.forEach(delegates, new Consumer<CommandComponentFactory>() {
            @Override
            public void accept(CommandComponentFactory factory) {
                componentBeanHolder.set(factory.get(type));
            }
        }, new Predicate<CommandComponentFactory>(){
            @Override
            public boolean test(CommandComponentFactory factory) {
                return !componentBeanHolder.isNull();
            }
        });
        return componentBeanHolder.get();
    }
}
