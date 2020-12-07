package com.jn.agileway.spring.propertysource;

import com.jn.langx.util.Emptys;
import org.springframework.core.env.PropertySources;

public class SpringPropertySources {
    public static Object getProperty(PropertySources ps, String key) {
        if (Emptys.isEmpty(key)) {
            return null;
        }
        return ps.get(key);
    }
}
