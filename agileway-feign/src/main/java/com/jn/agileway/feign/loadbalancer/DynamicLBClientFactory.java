package com.jn.agileway.feign.loadbalancer;

import com.jn.agileway.feign.HttpConnectionContext;
import com.jn.langx.util.Objs;
import com.netflix.client.ClientFactory;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import feign.ribbon.LBClient;
import feign.ribbon.LBClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.netflix.client.config.CommonClientConfigKey.ListOfServers;

/**
 * @since 1.0.0
 */
public class DynamicLBClientFactory implements LBClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(DynamicLBClientFactory.class);
    private HttpConnectionContext context;

    public DynamicLBClientFactory(HttpConnectionContext context) {
        this.context = context;
    }

    /**
     * 每次发起请求时，都会调用该方法
     *
     * @param clientName licenseServer
     * @return LoadBalance Request Client
     */
    @Override
    public LBClient create(String clientName) {
        IClientConfig config = ClientFactory.getNamedConfig(clientName, LBClientConfig.class);

        // listOfServers
        String listOfServers = config.get(ListOfServers);
        String nodesString = context.getNodesString();
        if (!Objs.equals(listOfServers, nodesString)) {
            logger.info("update nodes from {} to {}", listOfServers, nodesString);
            config.set(ListOfServers, nodesString);
        }

        // default port
        int port = config.get(CommonClientConfigKey.Port);
        int defaultPort = context.getConfiguration().getDefaultPort();
        if(defaultPort!=port){
            config.set(CommonClientConfigKey.Port, defaultPort);
        }

        ILoadBalancer lb = ClientFactory.getNamedLoadBalancer(clientName);
        return LBClient.create(lb, config);
    }
}
