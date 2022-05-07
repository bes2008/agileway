package com.jn.agileway.http.rest;

import com.jn.langx.Ordered;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;

import java.util.List;

public class RestActionExceptionHandlerRegistration implements Ordered {
    private final List<RestActionExceptionHandlerDefinition> exceptionClassConfigs = Collects.newArrayList();
    private String name;
    private int order;

    @NonNull
    private RestActionExceptionHandler exceptionHandler;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExceptionHandler(RestActionExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public RestActionExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void addExceptionClass(RestActionExceptionHandlerDefinition definition) {
        if (definition != null && definition.isValid()) {
            exceptionClassConfigs.add(definition);
        }
    }

    public boolean isExtendsEnabled(){
        return Collects.anyMatch(exceptionClassConfigs, new Predicate<RestActionExceptionHandlerDefinition>() {
            @Override
            public boolean test(RestActionExceptionHandlerDefinition element) {
                return element.isSupportExtends();
            }
        });
    }

    public RestActionExceptionHandlerDefinition findMatchedRegistration(final Throwable exception, final boolean causeScanEnabled){
        return Collects.findFirst(exceptionClassConfigs, new Predicate<RestActionExceptionHandlerDefinition>() {
            @Override
            public boolean test(RestActionExceptionHandlerDefinition element) {

                Class exceptionClass = element.getExceptionClass();

                if(exception.getClass()==exceptionClass){
                    return true;
                }

                if(element.isSupportExtends()){
                    if(Reflects.isSubClassOrEquals(exceptionClass, exception.getClass())){
                        return true;
                    }
                }

                if(causeScanEnabled){
                    if( exception.getCause() != null && exception.getCause() != exception){
                        return findMatchedRegistration(exception.getCause(), causeScanEnabled)!=null;
                    }
                }
                return false;
            }
        });
    }


}
