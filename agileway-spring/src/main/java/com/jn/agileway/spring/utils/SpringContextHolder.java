package com.jn.agileway.spring.utils;

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

    private static CountDownLatch downLatch = new CountDownLatch(1);

    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        SpringContextHolder.applicationContext = appContext;
        downLatch.countDown();
    }

    public static ApplicationContext getApplicationContext() {
        if (downLatch.getCount() < 1) {
            return applicationContext;
        }
        try {
            downLatch.await();
            return applicationContext;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T getBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

}
