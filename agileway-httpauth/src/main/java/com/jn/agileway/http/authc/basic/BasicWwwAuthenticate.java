package com.jn.agileway.http.authc.basic;

import com.jn.agileway.http.authc.AuthScheme;
import com.jn.agileway.http.authc.WwwAuthenticate;
import com.jn.langx.annotation.Nullable;

/**
 * <pre>
 * 参考链接：https://datatracker.ietf.org/doc/html/rfc7617#section-2
 * </pre>
 */
public class BasicWwwAuthenticate extends WwwAuthenticate {

    public BasicWwwAuthenticate() {
        super();
        setAuthScheme(AuthScheme.BASIC.getScheme());
    }

    /**
     * 可选的，只允许 值为UTF-8
     */
    @Nullable
    public String getCharset() {
        return getField("charset");
    }

    public void setCharset(String charset) {
        charset = "UTF-8";
        setField("charset", charset, false);
    }

}
