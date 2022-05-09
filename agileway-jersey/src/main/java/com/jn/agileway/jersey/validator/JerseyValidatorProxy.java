package com.jn.agileway.jersey.validator;

import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import org.glassfish.jersey.server.model.Invocable;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import java.util.Set;

public class JerseyValidatorProxy implements ConfiguredValidator {
    protected ConfiguredValidator delegate;

    @Override
    public void validateResourceAndInputParams(Object resource, Invocable resourceMethod, Object[] args) throws ConstraintViolationException {
        if (delegate != null) {
            delegate.validateResourceAndInputParams(resource, resourceMethod, args);
        }
    }

    @Override
    public void validateResult(Object resource, Invocable resourceMethod, Object result) throws ConstraintViolationException {
        if (delegate != null) {
            delegate.validateResult(resource, resourceMethod, result);
        }
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return delegate == null ? null : delegate.validate(object, groups);
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        return delegate == null ? null : delegate.validateProperty(object, propertyName, groups);
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        return delegate == null ? null : delegate.validateValue(beanType, propertyName, value, groups);
    }

    @Override
    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        return delegate == null ? null : delegate.getConstraintsForClass(clazz);
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return delegate == null ? null : delegate.unwrap(type);
    }

    @Override
    public ExecutableValidator forExecutables() {
        return delegate == null ? null : delegate.forExecutables();
    }
}
