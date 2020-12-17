package com.jn.agileway.redis.core.script;

import com.jn.langx.configuration.resource.ResourceConfigurationLoader;
import com.jn.langx.io.resource.*;
import com.jn.langx.util.io.IOs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class RedisLuaScriptResourceLoader extends ResourceConfigurationLoader<RedisLuaScript> {
    private static final Logger logger = LoggerFactory.getLogger(RedisLuaScriptResourceLoader.class);

    @Override
    public RedisLuaScript load(String configurationId) {
        Location location = this.getResourceLocationProvider().get(configurationId);
        if (location == null) {
            logger.warn("Can't find the location for configuration : {}", configurationId);
        }

        RedisLuaScript configuration = null;
        Resource resource = Resources.loadResource(location);
        if (resource != null && resource.exists()) {
            InputStream inputStream = null;

            try {
                inputStream = resource.getInputStream();
                configuration = this.getParser().parse(inputStream);
                if (configuration != null) {
                    configuration.setId(configurationId);
                    Object realResource = resource.getRealResource();
                    if (resource instanceof FileResource) {
                        configuration.setLocation(new FileSystemResource((File) realResource));
                    } else if (resource instanceof ClassPathResource) {
                        configuration.setLocation(new org.springframework.core.io.ClassPathResource(location.getPath()));
                    }
                }
            } catch (IOException var7) {
                logger.error(var7.getMessage(), var7);
            } finally {
                IOs.close(inputStream);
            }
        } else {
            logger.error("Location {} is not exists", location);
        }

        return configuration;
    }
}
