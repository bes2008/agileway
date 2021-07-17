package com.jn.agileway.feign.loadbalancer;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfigKey;

/**
 * 由框架控制，只创建一个实例，不需要我们代码调用
 * @since 1.0.0
 */
public class LBClientConfig extends DefaultClientConfigImpl {
    IClientConfigKey<String> retryableStatusCodes = new CommonClientConfigKey<String>("RetryableStatusCodes") {
    };

    @Override
    public int getDefaultMaxAutoRetriesNextServer() {
        return 0;
    }

    @Override
    public void loadDefaultValues() {
        super.loadDefaultValues();
        putDefaultStringProperty(retryableStatusCodes, "");
    }
}
