package com.jn.agileway.spring.propertysource;

import com.jn.langx.Delegatable;
import com.jn.langx.propertyset.PropertySet;
import org.springframework.core.env.PropertySources;

public class SpringPropertySourcesAdapter implements PropertySet, Delegatable<PropertySources> {
    private String name;
    private PropertySources delegate;

    @Override
    public Object getSource() {
        return this.delegate;
    }

    @Override
    public PropertySources getDelegate() {
        return this.delegate;
    }

    @Override
    public void setDelegate(PropertySources propertySources) {
        this.delegate = propertySources;
    }

    @Override
    public boolean containsProperty(String key) {
        return this.delegate.contains(key);
    }

    @Override
    public Object getProperty(String key) {
        Object obj = this.delegate.get(key);
        if (obj != null) {
            return obj.toString();
        }
        return null;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
