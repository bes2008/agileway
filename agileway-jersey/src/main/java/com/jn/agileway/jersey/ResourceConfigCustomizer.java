package com.jn.agileway.jersey;

import com.jn.langx.Customizer;
import org.glassfish.jersey.server.ResourceConfig;

public interface ResourceConfigCustomizer extends Customizer<ResourceConfig> {
    @Override
    void customize(ResourceConfig rc);
}
