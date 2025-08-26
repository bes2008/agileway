package com.jn.agileway.spring.propertysource;

import com.jn.langx.propertyset.AbstractPropertySet;
import org.springframework.core.env.Environment;

public class SpringEnvironmentPropertySet extends AbstractPropertySet<Environment> {

    public SpringEnvironmentPropertySet(String name, Environment source) {
        super(name, source);
    }

    @Override
    public Object getProperty(String key) {
        return getSource().getProperty(key);
    }
}
