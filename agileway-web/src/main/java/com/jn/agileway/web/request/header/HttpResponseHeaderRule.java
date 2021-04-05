package com.jn.agileway.web.request.header;

import com.jn.agileway.web.prediate.HttpRequestPredicateConfigItems;

public class HttpResponseHeaderRule extends HttpRequestPredicateConfigItems {
    private String header;
    private String value;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
