package com.jn.agileway.httpclient.test.core;

import com.jn.agileway.httpclient.core.HttpExchanger;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.jdk.JdkUnderlyingHttpRequestFactory;
import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.util.collection.multivalue.CommonMultiValueMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.net.http.HttpHeaders;
import org.junit.Test;

import java.util.Map;

public class NacosApiTests {
    private String baseUri = "http://localhost:8848/nacos";

    private HttpExchanger initExchanger() {
        HttpExchanger exchanger = new HttpExchanger();
        exchanger.setRequestFactory(new JdkUnderlyingHttpRequestFactory());
        exchanger.init();
        return exchanger;
    }

    @Test
    public void listClusterNodes() {
        HttpExchanger httpExchanger = initExchanger();
        MultiValueMap<String, Object> params = new CommonMultiValueMap<>();
        params.add("serviceName", "com.jn.agileway.test");
        params.add("withInstances", false);
        params.add("pageNo", 1);
        params.add("pageSize", 10);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Accept", "application/json");
        requestHeaders.add("Accept", "text/javascript");
        requestHeaders.add("Accept", "*/*; q=0.01");

        requestHeaders.add("Accept-Encoding", "gzip");
        requestHeaders.add("Accept-Encoding", "deflate");
        requestHeaders.add("Accept-Encoding", "br");

        HttpRequest request = HttpRequest.forGet(baseUri + "/v1/core/cluster/nodes", params, null, requestHeaders);
        HttpResponse<Map> response = httpExchanger.<Map>exchange(false, request, Map.class)
                .await();
        System.out.println(JSONs.toJson(response, true));

    }
}
