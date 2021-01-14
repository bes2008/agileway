package com.jn.agileway.spring.propertysource;

import com.jn.langx.util.BasedStringAccessor;
import com.jn.langx.util.Preconditions;
import org.springframework.core.env.PropertyResolver;

public class SpringPropertyResolverAccessor extends BasedStringAccessor<String, PropertyResolver> {

    @Override
    public Object get(String key) {
        return getString(key, (String) null);
    }

    @Override
    public String getString(String key, String defaultValue) {
        Preconditions.checkNotEmpty(key, "the property name is null or empty");
        PropertyResolver propertySource = getTarget();
        return propertySource.getProperty(key, defaultValue);
    }

    @Override
    public void set(String key, Object value) {
        // ignore it
    }

    @Override
    public void remove(String key) {
        // ignore it
    }
}
