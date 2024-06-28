package com.jn.agileway.redis.core.script;

import com.jn.langx.io.resource.Location;
import com.jn.langx.io.resource.ResourceLocationProvider;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;

public class BuiltinLuaScriptLocationProvider implements ResourceLocationProvider<String>, Initializable {
    private static final Logger logger = Loggers.getLogger(BuiltinLuaScriptLocationProvider.class);
    private String builtinPackageClassPath;


    @Override
    public void init() throws InitializationException {
        if (builtinPackageClassPath == null) {
            String packageName = Reflects.getPackageName(BuiltinLuaScriptLocationProvider.class);
            packageName = packageName.replace('.', '/');
            packageName = "/" + packageName + "/lua/";
            builtinPackageClassPath = packageName;
            logger.info("Initial the built-in redis lua script location: {}", "classpath:" + builtinPackageClassPath);
        }
    }

    @Override
    public Location get(String scriptId) {
        return new Location("classpath:", builtinPackageClassPath + scriptId + ".lua");
    }
}
