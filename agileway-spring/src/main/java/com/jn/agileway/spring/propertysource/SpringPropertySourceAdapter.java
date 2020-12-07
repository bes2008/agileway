package com.jn.agileway.spring.propertysource;

import com.jn.langx.Delegatable;
import com.jn.langx.text.PropertySource;
import com.jn.langx.util.Strings;

public class SpringPropertySourceAdapter implements PropertySource, Delegatable<org.springframework.core.env.PropertySource> {
    private org.springframework.core.env.PropertySource delegate;
    private String name;

    public SpringPropertySourceAdapter(org.springframework.core.env.PropertySource propertySource) {
        this.setDelegate(propertySource);
    }

    public void setDelegate(org.springframework.core.env.PropertySource delegate) {
        this.delegate = delegate;
        setName(this.delegate.getName());
    }

    @Override
    public org.springframework.core.env.PropertySource getDelegate() {
        return this.delegate;
    }

    @Override
    public boolean containsProperty(String key) {
        return this.delegate.containsProperty(key);
    }

    @Override
    public String getProperty(String key) {
        Object obj = this.delegate.getProperty(key);
        if (obj != null) {
            return obj.toString();
        }
        return null;
    }

    @Override
    public void setName(String sourceName) {
        this.name = sourceName;
    }

    @Override
    public String getName() {
        return Strings.useValueIfBlank(this.name, this.delegate.getName());
    }
}
