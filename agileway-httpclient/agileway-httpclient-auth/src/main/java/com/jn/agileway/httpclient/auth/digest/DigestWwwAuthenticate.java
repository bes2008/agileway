package com.jn.agileway.httpclient.auth.digest;

import com.jn.agileway.httpclient.auth.AuthScheme;
import com.jn.agileway.httpclient.auth.WwwAuthenticate;
import com.jn.agileway.httpclient.auth.WwwAuthenticateUtils;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Strings;
import com.jn.langx.util.enums.Enums;

import java.util.List;

/**
 * For historical reasons, a sender MUST only generate the quoted string
 * syntax values for the following parameters: realm, domain, nonce,
 * opaque, and qop.
 *
 * <a href="https://datatracker.ietf.org/doc/html/rfc7616">rfc7616</a>
 */
public class DigestWwwAuthenticate extends WwwAuthenticate {

    public DigestWwwAuthenticate() {
        super();
        setAuthScheme(AuthScheme.DIGEST.getScheme());
    }


    public void setDomain(String domain) {
        setField("domain", domain, true);
    }

    /**
     * 指定哪些资源可以访问，默认是所有。它的值是一个uri 列表，用空格分隔。
     * 值一定有双引号
     */
    public String getDomain() {
        return getField("domain");
    }

    public List<String> getDomainAsList() {
        String domain = getDomain();
        return WwwAuthenticateUtils.getFieldAsList(domain, " ");
    }

    /**
     * 指定nonce的生成时间，格式是 RFC 1123 date，
     * 值一定有双引号
     */
    public void setNonce(String nonce) {
        setField("nonce", nonce, true);
    }

    public String getNonce() {
        return getField("nonce");
    }


    /**
     * A string of data, specified by the server, that SHOULD be returned
     * by the client unchanged in the Authorization header field of
     * subsequent requests with URIs in the same protection space.  It is
     * RECOMMENDED that this string be Base64 or hexadecimal data.
     * <p>
     * 值一定有双引号
     */
    public String getOpaque() {
        return getField("opaque");
    }

    public void setOpaque(String opaque) {
        setField("opaque", opaque, true);
    }

    public void setStale(boolean stale) {
        if (stale) {
            setField("stale", "true");
        }
    }

    /**
     * 指定之前的请求中的nonce是否过时。当 stale 为 true 时，则表示之前的请求已经过时了，需要重新请求，并且不需要传入 username, password.
     * 如果没有出现 stale 参数，或者stale的值不是 true时，则表示之前的请求中的username 或者|和 password 是无效的，需要重新输入。
     * <p>
     * A case-insensitive flag indicating that the previous request from
     * the client was rejected because the nonce value was stale.  If
     * stale is true, the client may wish to simply retry the request
     * with a new encrypted response, without re-prompting the user for a
     * new username and password.  The server SHOULD only set stale to
     * true if it receives a request for which the nonce is invalid.  If
     * stale is false, or anything other than true, or the stale
     * parameter is not present, the username and/or password are
     * invalid, and new values MUST be obtained.
     * </p>
     */
    public boolean isStale() {
        String stale = getField("stale");
        return "true".equals(stale);
    }

    public void setAlgorithm(DigestAlgorithm algorithm) {
        setField("algorithm", algorithm.getName(), false);
    }

    public DigestAlgorithm getAlgorithm() {
        String algorithm = getField("algorithm");
        if (Strings.isEmpty(algorithm)) {
            return null;
        }
        return Enums.ofName(DigestAlgorithm.class, algorithm);
    }

    /**
     * 指定qop的值，可选值有 auth 和 auth-int
     * 值一定有双引号
     */
    @NotEmpty
    public void setQop(String qop) {
        setField("qop", qop, true);
    }

    public String getQop() {
        return getField("qop");
    }

    public List<String> getQopAsList() {
        String qop = getQop();
        return WwwAuthenticateUtils.getFieldAsList(qop, ",");
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

    /**
     * This is an OPTIONAL parameter that is used by the server to
     * indicate that it supports username hashing.  Valid values are:
     * "true" or "false".  Default value is "false".
     */
    public void setUserhash(boolean userhash) {
        setField("userhash", userhash ? "true" : "false");
    }

    public boolean isUserhash() {
        String userhash = getField("userhash");
        return "true".equals(userhash);
    }

}
