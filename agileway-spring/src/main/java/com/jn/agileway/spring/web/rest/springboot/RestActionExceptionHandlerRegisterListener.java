package com.jn.agileway.spring.web.rest.springboot;

import com.jn.agileway.web.rest.GlobalRestExceptionHandler;
import com.jn.agileway.web.rest.GlobalRestExceptionHandlerRegistry;
import com.jn.agileway.web.rest.RestActionExceptionHandler;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RestActionExceptionHandlerRegisterListener implements ApplicationListener<ContextRefreshedEvent> {

    private GlobalRestExceptionHandlerRegistry exceptionHandlerRegistry;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        Map<String, RestActionExceptionHandler> handlerMap = applicationContext.getBeansOfType(RestActionExceptionHandler.class);
        Pipeline.of(Collects.filter(handlerMap, new Predicate2<String, RestActionExceptionHandler>() {
            @Override
            public boolean test(String key, RestActionExceptionHandler exceptionHandler) {
                return !(exceptionHandler instanceof GlobalRestExceptionHandler);
            }
        }).values()).forEach(new Consumer<RestActionExceptionHandler>() {
            @Override
            public void accept(RestActionExceptionHandler exceptionHandler) {
                exceptionHandlerRegistry.register(exceptionHandler);
            }
        });

    }

    @Autowired
    public void setExceptionHandlerRegistry(GlobalRestExceptionHandlerRegistry exceptionHandlerRegistry) {
        this.exceptionHandlerRegistry = exceptionHandlerRegistry;
    }
}
