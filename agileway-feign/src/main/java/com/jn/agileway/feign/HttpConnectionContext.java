package com.jn.agileway.feign;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.net.ClusterAddressParser;
import com.jn.langx.util.net.NetworkAddress;
import feign.Logger;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.List;

public class HttpConnectionContext {
    private HttpConnectionProperties configuration;
    /**
     * license server nodes
     */
    private List<NetworkAddress> nodes;

    private HttpClient httpClient;

    public List<NetworkAddress> getNodes() {
        if (nodes != null) {
            return nodes;
        }
        nodes = new ClusterAddressParser(configuration.getDefaultPort()).parse(configuration.getNodes());
        if (nodes == null) {
            Collects.emptyArrayList();
        }
        return nodes;
    }

    public String getNodesString() {
        return Strings.join(",", nodes);
    }

    public boolean isLoadbalancerEnabled() {
        return getNodes().size() > 1;
    }

    public Logger.Level getAccessLogLevel() {
        return Enums.ofName(feign.Logger.Level.class, configuration.getAccessLogLevel());
    }

    public HttpClient getHttpClient() {
        if (httpClient == null) {
            return HttpClients.createDefault();
        }
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpConnectionProperties getConfiguration() {
        return configuration;
    }

    public void setConfiguration(HttpConnectionProperties configuration) {
        this.configuration = configuration;
    }

    public String getUrl() {
        String url = null;
        if (Emptys.isNotEmpty(getNodes())) {
            url = getNodes().size() > 1 ? configuration.getLoadbalancerHost() : getNodes().get(0).toString();
        } else {
            url = "unknown";
        }
        url = StringTemplates.formatWithPlaceholder("{}://{}", configuration.getProtocol(), url);
        String urlPrefix = this.configuration.getPathPrefix();
        if (Strings.isBlank(urlPrefix)) {
            return url;
        }
        return url + "/" + urlPrefix;
    }
}
