package com.jn.agileway.web.rest;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;

import java.util.List;

public class RestActionExceptionHandlerRegistration {
    private final List<RestActionExceptionHandlerRegistrationElement> exceptionClassConfigs = Collects.newArrayList();
    private String name;
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

    public RestActionExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void addExceptionClass(RestActionExceptionHandlerRegistrationElement element) {
        if (element != null && element.isValid()) {
            exceptionClassConfigs.add(element);
        }
    }

    public boolean isExtendsEnabled(){
        return Collects.anyMatch(exceptionClassConfigs, new Predicate<RestActionExceptionHandlerRegistrationElement>() {
            @Override
            public boolean test(RestActionExceptionHandlerRegistrationElement element) {
                return element.isSupportExtends();
            }
        });
    }

    public RestActionExceptionHandlerRegistrationElement findMatchedRegistration(final Throwable exception, final boolean causeScanEnabled){
        return Collects.findFirst(exceptionClassConfigs, new Predicate<RestActionExceptionHandlerRegistrationElement>() {
            @Override
            public boolean test(RestActionExceptionHandlerRegistrationElement element) {

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
