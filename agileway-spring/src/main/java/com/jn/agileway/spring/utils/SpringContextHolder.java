package com.jn.agileway.spring.utils;

import com.jn.langx.util.logging.Loggers;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * SpringContextHolder 这个类，不是单例的。
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    private static final CountDownLatch downLatch = new CountDownLatch(1);

    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        if (SpringContextHolder.applicationContext == null) {
            SpringContextHolder.applicationContext = appContext;
            downLatch.countDown();
        }
    }

    public static ApplicationContext getApplicationContext() {
        if (SpringContextHolder.applicationContext == null) {
            try {
                downLatch.await();
                if(SpringContextHolder.applicationContext!=null) {
                    return SpringContextHolder.applicationContext;
                }else{
                    throw new IllegalStateException("can't find the application context");
                }
            } catch (InterruptedException ex) {
                Loggers.getLogger(SpringContextHolder.class).warn("application bootstrap failed ");
                return getApplicationContext();
            }
        }
        return SpringContextHolder.applicationContext;
    }

    public static <T> T getBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

}
