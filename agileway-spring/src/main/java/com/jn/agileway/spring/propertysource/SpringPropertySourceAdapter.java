package com.jn.agileway.spring.propertysource;

import com.jn.langx.Delegatable;
import com.jn.langx.propertyset.PropertySet;
import com.jn.langx.util.Strings;
import org.springframework.core.env.PropertySource;

public class SpringPropertySourceAdapter implements PropertySet, Delegatable<org.springframework.core.env.PropertySource> {
    private PropertySource delegate;
    private String name;

    @Override
    public Object getSource() {
        return this.delegate;
    }

    public SpringPropertySourceAdapter(PropertySource propertySource) {
        this.setDelegate(propertySource);
    }

    public void setDelegate(PropertySource delegate) {
        this.delegate = delegate;
        setName(this.delegate.getName());
    }

    @Override
    public PropertySource getDelegate() {
        return this.delegate;
    }

    @Override
    public boolean containsProperty(String key) {
        return this.delegate.containsProperty(key);
    }

    @Override
    public Object getProperty(String key) {
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
