package com.jn.agileway.audit.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConditionalOnMissingBean(name = "operationDefinitionProperties")
@ConfigurationProperties(prefix = "operation.definition")
public class OperationDefinitionProperties {

    private String location = "classpath:/operation.yml";
    private int reloadIntervalInSeconds = -1;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getReloadIntervalInSeconds() {
        return reloadIntervalInSeconds;
    }

    public void setReloadIntervalInSeconds(int reloadIntervalInSeconds) {
        this.reloadIntervalInSeconds = reloadIntervalInSeconds;
    }
}
