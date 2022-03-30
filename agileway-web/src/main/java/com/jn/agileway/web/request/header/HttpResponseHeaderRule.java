package com.jn.agileway.web.request.header;

import com.jn.agileway.web.prediate.HttpRequestPredicateConfigItems;

/**
 * 提供 满足 predicates 条件的情况下，为 response 设置 header
 */
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
