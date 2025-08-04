package com.jn.agileway.oauth2.authz;

import com.jn.agileway.oauth2.authz.api.std.StandardOpenOAuth2Properties;
import com.jn.langx.util.collection.Lists;

import java.time.Duration;
import java.util.List;

public class OAuth2Properties {
    /**
     * OAuth2 服务器的地址
     */
    private String baseUri;


    /**
     * 获取授权码的uri模板
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-4.1.1">rfc6749</a>
     * @see #authorizeUriEncoding
     */
    private String authorizeUriTemplate = "/authorize?response_type=code&client_id={client_id}&redirect_uri={redirect_uri}&state={state}&scope={scope}";

    private List<String> authorizeUriScopes = Lists.newArrayList("userinfo", "openid");

    /**
     * OAuth2 服务器的 /authorize uri 的编码
     *
     * @see #authorizeUriTemplate
     */
    private String authorizeUriEncoding = "ISO-8859-1";

    /**
     * UM3rd 在OAuth2 服务器中的clientId
     */
    private String clientId;

    /**
     * UM3rd 在OAuth2 服务器中的clientSecret
     */
    private String clientSecret;

    /**
     * 获取授权码的回调地址，它是um3rd提供的endpoint，需要注册在oauth2 服务器中
     */
    private String callbackUri = "/auth/oauth2/callback";


    private boolean userinfoEndpointEnabled = true;
    private boolean introspectEndpointEnabled = true;


    private boolean extractUserinfoWithIdToken = true;
    private boolean extractUserinfoWithIntrospectResult = true;

    /**
     * Um3rd 提供的登录的uri 中的access-token参数名
     */
    private String accessTokenParameterName = "access_token";

    private String accessTokenCookieName = "OAUTH2_ACCESS_TOKEN";

    /**
     * 访问 CAS的 API 的请求超时时间
     */
    private Duration readTimeout = Duration.ofSeconds(30);
    private StandardOpenOAuth2Properties standard = new StandardOpenOAuth2Properties();

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAuthorizeUriTemplate() {
        return authorizeUriTemplate;
    }

    public void setAuthorizeUriTemplate(String authorizeUriTemplate) {
        this.authorizeUriTemplate = authorizeUriTemplate;
    }


    public String getAuthorizeUriEncoding() {
        return authorizeUriEncoding;
    }

    public void setAuthorizeUriEncoding(String authorizeUriEncoding) {
        this.authorizeUriEncoding = authorizeUriEncoding;
    }

    public String getAccessTokenParameterName() {
        return accessTokenParameterName;
    }

    public void setAccessTokenParameterName(String accessTokenParameterName) {
        this.accessTokenParameterName = accessTokenParameterName;
    }

    public String getAccessTokenCookieName() {
        return accessTokenCookieName;
    }

    public void setAccessTokenCookieName(String accessTokenCookieName) {
        this.accessTokenCookieName = accessTokenCookieName;
    }

    public String getCallbackUri() {
        return callbackUri;
    }

    public void setCallbackUri(String callbackUri) {
        this.callbackUri = callbackUri;
    }


    public StandardOpenOAuth2Properties getStandard() {
        return standard;
    }

    public void setStandard(StandardOpenOAuth2Properties standard) {
        this.standard = standard;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public boolean isUserinfoEndpointEnabled() {
        return userinfoEndpointEnabled;
    }

    public void setUserinfoEndpointEnabled(boolean userinfoEndpointEnabled) {
        this.userinfoEndpointEnabled = userinfoEndpointEnabled;
    }

    public boolean isIntrospectEndpointEnabled() {
        return introspectEndpointEnabled;
    }

    public void setIntrospectEndpointEnabled(boolean introspectEndpointEnabled) {
        this.introspectEndpointEnabled = introspectEndpointEnabled;
    }

    public List<String> getAuthorizeUriScopes() {
        return authorizeUriScopes;
    }

    public void setAuthorizeUriScopes(List<String> authorizeUriScopes) {
        this.authorizeUriScopes = authorizeUriScopes;
    }

    public boolean isExtractUserinfoWithIdToken() {
        return extractUserinfoWithIdToken;
    }

    public void setExtractUserinfoWithIdToken(boolean extractUserinfoWithIdToken) {
        this.extractUserinfoWithIdToken = extractUserinfoWithIdToken;
    }

    public boolean isExtractUserinfoWithIntrospectResult() {
        return extractUserinfoWithIntrospectResult;
    }

    public void setExtractUserinfoWithIntrospectResult(boolean extractUserinfoWithIntrospectResult) {
        this.extractUserinfoWithIntrospectResult = extractUserinfoWithIntrospectResult;
    }
}
