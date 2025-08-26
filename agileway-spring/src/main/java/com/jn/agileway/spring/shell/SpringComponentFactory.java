package com.jn.agileway.spring.shell;

import com.jn.agileway.shell.exec.CommandComponentFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringComponentFactory implements CommandComponentFactory , ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object get(Class type) {
        try {
            return applicationContext.getBean(type);
        }catch (NoSuchBeanDefinitionException e){
            return null;
        }catch (BeansException e){
            return null;
        }
    }
}
