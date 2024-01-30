package com.jn.agileway.web.prediate;

import com.jn.langx.text.StringTemplates;

public class HttpRequestPredicateConfigItem {
    private String key;
    private String configuration;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        return StringTemplates.formatWithPlaceholder("{}={}", key,configuration);
    }
}
