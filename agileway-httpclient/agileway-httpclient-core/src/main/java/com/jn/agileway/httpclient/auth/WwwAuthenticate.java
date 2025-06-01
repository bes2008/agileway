package com.jn.agileway.httpclient.auth;

import com.jn.langx.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

import com.jn.langx.util.Strings;

/**
 * 这个是响应头。用户告诉 UserAgent ，当前请求需要使用哪种方式进行认证
 * <p>
 * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Reference/Headers/WWW-Authenticate
 */
public class WwwAuthenticate {
    private String authScheme;
    private Map<String, String> fields = new LinkedHashMap<String, String>();

    public String getAuthScheme() {
        return authScheme;
    }

    public void setAuthScheme(String authScheme) {
        this.authScheme = authScheme;
    }

    /**
     * 指定从哪个 realm 获取凭证，
     * 值一定有双引号
     */
    public void setRealm(String realm) {
        setField("realm", realm, true);
    }

    @Nullable
    public String getRealm() {
        return getField("realm");
    }


    public Map<String, String> getFields() {
        return fields;
    }

    public final String getField(String fieldName) {
        return Strings.unquoted(fields.get(fieldName));
    }

    public final void setField(String fieldName, String fieldValue) {
        setField(fieldName, fieldValue, false);
    }

    public final void setField(String fieldName, String fieldValue, boolean quoted) {
        this.fields.put(fieldName, quoted ? Strings.quoted(fieldValue) : Strings.unquoted(fieldValue));
    }

    @Override
    public String toString() {
        return toHeaderValue();
    }

    public String toHeaderValue() {
        return WwwAuthenticateUtils.buildWwwAuthenticateHeaderValue(this);
    }
}
