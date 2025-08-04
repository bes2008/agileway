package com.jn.agileway.oauth2.authz.api.std;

import com.jn.langx.util.net.mime.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.Map;

/**
 * <pre>
 *    @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#UserInfo">OpenID: UserInfo</a>
 *    @see <a href="https://www.oauth.com/oauth2-servers/token-introspection-endpoint/">token-introspection-endpoint</a>
 * </pre>
 */
@HttpExchange
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
    @PostExchange(value = "/token", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<Map<String, ?>> getToken(
            @RequestHeader("Authorization") String basicAuthToken,
            @RequestParam(value = "grant_type", defaultValue = "authorization_code") String grantType,
            @RequestParam("code") String code,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("client_id") String clientId
    );

    /**
     * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#RefreshTokens">Refresh Tokens</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-6">Refresh Tokens</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-3.2">Access Token Response</a>
     * <p>
     * content-type为：application/x-www-form-urlencoded，@RequestParam 的参数会放在 Body 中
     */
    @PostExchange(value = "/token", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<Map<String, ?>> refreshToken(
            @RequestHeader("Authorization") String basicAuthToken,
            @RequestParam(value = "grant_type", defaultValue = "refresh_token") String grantType,
            @RequestParam("refresh_token") String refreshToken,
            @RequestParam("client_id") String clientId
    );


    /**
     * @see <a href="https://www.oauth.com/oauth2-servers/token-introspection-endpoint/">token-introspection-endpoint</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc7662#section-2.1">Introspection Request</a>
     */
    @GetExchange(value = "/introspect?{tokenParameterName}={token}")
    ResponseEntity<Map<String, ?>> introspectByGet(
            @RequestHeader("Authorization") String authToken,
            @PathVariable("tokenParameterName") String tokenParameterName,
            @PathVariable("token") String token,
            @RequestParam("token_type_hint") String tokenTypeHint
    );

    /**
     * 这个端点的名字不确定，可以叫introspection，也可以叫token-introspection，也可以叫token-info。
     *
     * @see <a href="https://www.oauth.com/oauth2-servers/token-introspection-endpoint/">token-introspection-endpoint</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc7662#section-2.1">Introspection Request</a>
     * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#IntrospectionEndpoint">Introspection Endpoint</a>
     */
    @PostExchange(value = "/introspect", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<Map<String, ?>> introspectByPost(
            @RequestHeader("Authorization") String authToken,
            @RequestParam("token_type_hint") String tokenTypeHint,
            @RequestBody Map<String, String> token);

    /**
     * 由OpenID 规范提供
     *
     * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#UserInfo">UserInfo</a>
     */
    @GetExchange(value = "/userinfo")
    ResponseEntity<Map<String, ?>> getUserInfoByGet(
            @RequestHeader("Authorization") String accessToken // 必须指定为 Bearer 类型
    );

    @PostExchange(value = "/userinfo")
    ResponseEntity<Map<String, ?>> getUserInfoByPost(
            @RequestHeader("Authorization") String accessToken // 必须指定为 Bearer 类型
    );

}
