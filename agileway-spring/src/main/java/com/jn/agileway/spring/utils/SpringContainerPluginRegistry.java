package com.jn.agileway.spring.utils;

import com.jn.langx.plugin.Plugin;
import com.jn.langx.plugin.PluginRegistry;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class SpringContainerPluginRegistry implements PluginRegistry {
    @Override
    public List<Plugin> plugins() {
        ApplicationContext ac = SpringContextHolder.getApplicationContext();
        return Collects.newArrayList(ac.getBeansOfType(Plugin.class).values());
    }

    @Override
    public <P extends Plugin> List<P> find(Class<P> itfc) {
        return find(Functions.isInstancePredicate(itfc));
    }

    @Override
    public <P extends Plugin> List<P> find(Predicate<P> predicate) {
        return Pipeline.<P>of(this.plugins()).filter(predicate).asList();
    }

    @Override
    public <P extends Plugin> List<P> find(Class<P> itfc, Predicate<P> predicate) {
        return  Pipeline.<P>of(find(itfc)).filter(predicate).asList();
    }

    @Override
    public <P extends Plugin> P findOne(Class<P> itfc, Predicate<P> predicate) {
        return Pipeline.of(find(itfc, predicate)).findFirst();
    }

    @Override
    public void register(Plugin plugin) {
        register(plugin.getName(), plugin);
    }

    @Override
    public void register(String key, Plugin plugin) {
        DefaultListableBeanFactory ctx = (DefaultListableBeanFactory)SpringContextHolder.getApplicationContext();
        ctx.registerSingleton(key, plugin);
    }

    @Override
    public void unregister(String key) {
        DefaultListableBeanFactory ctx = (DefaultListableBeanFactory)SpringContextHolder.getApplicationContext();
        ctx.removeBeanDefinition(key);
    }

    @Override
    public boolean contains(String key) {
        DefaultListableBeanFactory ctx = (DefaultListableBeanFactory)SpringContextHolder.getApplicationContext();
        return ctx.containsBean(key);
    }

    @Override
    public Plugin get(String key) {
        return SpringContextHolder.getBean(key);
    }
}
