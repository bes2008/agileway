package com.jn.agileway.oauth2.authz.api.std;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.declarative.anno.*;
import com.jn.langx.util.net.mime.MediaType;

import java.util.Map;

/**
 * <pre>
 *    @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#UserInfo">OpenID: UserInfo</a>
 *    @see <a href="https://www.oauth.com/oauth2-servers/token-introspection-endpoint/">token-introspection-endpoint</a>
 * </pre>
 */
@HttpEndpoint
public interface StandardOpenIdOAuth2Api {
    /**
     * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#TokenEndpoint">Token Endpoint</a>
     * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#RefreshTokens">Refresh Tokens</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-4.1.3">Access Token Request</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-3.2">Access Token Response</a>
     * <p>
     * <p>
     * content-type为：application/x-www-form-urlencoded，@RequestParam 的参数会放在 Body 中
     */
    @Post(value = "/token", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    HttpResponse<Map<String, ?>> getToken(
            @Header("Authorization") String basicAuthToken,
            @QueryParam(value = "grant_type", defaultValue = "authorization_code") String grantType,
            @QueryParam("code") String code,
            @QueryParam("redirect_uri") String redirectUri,
            @QueryParam("client_id") String clientId
    );

    /**
     * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#RefreshTokens">Refresh Tokens</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-6">Refresh Tokens</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-3.2">Access Token Response</a>
     * <p>
     * content-type为：application/x-www-form-urlencoded，@RequestParam 的参数会放在 Body 中
     */
    @Post(value = "/token", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    HttpResponse<Map<String, ?>> refreshToken(
            @Header("Authorization") String basicAuthToken,
            @BodyPart(value = "grant_type") String grantType, // 值为 refresh_token
            @BodyPart("refresh_token") String refreshToken,
            @BodyPart("client_id") String clientId
    );


    /**
     * @see <a href="https://www.oauth.com/oauth2-servers/token-introspection-endpoint/">token-introspection-endpoint</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc7662#section-2.1">Introspection Request</a>
     */
    @Get(value = "/introspect?{tokenParameterName}={token}")
    HttpResponse<Map<String, ?>> introspectByGet(
            @Header("Authorization") String authToken,
            @UriVariable("tokenParameterName") String tokenParameterName,
            @UriVariable("token") String token,
            @QueryParam("token_type_hint") String tokenTypeHint
    );

    /**
     * 这个端点的名字不确定，可以叫introspection，也可以叫token-introspection，也可以叫token-info。
     *
     * @see <a href="https://www.oauth.com/oauth2-servers/token-introspection-endpoint/">token-introspection-endpoint</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc7662#section-2.1">Introspection Request</a>
     * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#IntrospectionEndpoint">Introspection Endpoint</a>
     */
    @Post(value = "/introspect", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    HttpResponse<Map<String, ?>> introspectByPost(
            @Header("Authorization") String authToken,
            @QueryParam("token_type_hint") String tokenTypeHint,
            @Body Map<String, String> token);

    /**
     * 由OpenID 规范提供
     *
     * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#UserInfo">UserInfo</a>
     */
    @Get(value = "/userinfo")
    HttpResponse<Map<String, ?>> getUserInfoByGet(
            @Header("Authorization") String accessToken // 必须指定为 Bearer 类型
    );

    @Post(value = "/userinfo")
    HttpResponse<Map<String, ?>> getUserInfoByPost(
            @Header("Authorization") String accessToken // 必须指定为 Bearer 类型
    );

}
