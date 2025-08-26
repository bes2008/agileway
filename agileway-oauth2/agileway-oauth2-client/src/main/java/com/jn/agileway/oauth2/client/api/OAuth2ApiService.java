package com.jn.agileway.oauth2.client.api;


import com.jn.agileway.oauth2.client.IntrospectResult;
import com.jn.agileway.oauth2.client.OAuth2Token;
import com.jn.agileway.oauth2.client.exception.InvalidAccessTokenException;
import com.jn.agileway.oauth2.client.userinfo.OpenIdUserinfo;

public interface OAuth2ApiService {

    OAuth2Token authorizeToken(String clientId, String clientSecret, String code, String redirectUri);

    OAuth2Token refreshToken(String clientId, String clientSecret, String refreshToken);

    /**
     * 根据 token 获取用户信息，如果token无效或者过期，则抛出异常，不能返回null
     *
     * @param token 令牌
     * @return 用户信息
     */
    OpenIdUserinfo userInfo(String token) throws InvalidAccessTokenException;

    boolean userInfoEndpointEnabled();

    boolean tokenIntrospectionEndpointEnabled();

    /**
     * @param tokenReference
     * @param tokenTypeHint  token 的类型，比如 "access_token", "refresh_token"
     * @throws InvalidAccessTokenException
     */
    IntrospectResult introspect(String tokenReference, String tokenTypeHint) throws InvalidAccessTokenException;


}
