package com.jn.agileway.oauth2.client.api.std;

import com.jn.agileway.httpclient.auth.AuthorizationHeaders;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.error.exception.UnauthorizedException;
import com.jn.agileway.oauth2.client.GrantType;
import com.jn.agileway.oauth2.client.IntrospectResult;
import com.jn.agileway.oauth2.client.OAuth2ClientProperties;
import com.jn.agileway.oauth2.client.OAuth2Token;
import com.jn.agileway.oauth2.client.api.IntrospectEndpointAuthTokenSupplier;
import com.jn.agileway.oauth2.client.api.OAuth2ApiResponseConverter;
import com.jn.agileway.oauth2.client.api.OAuth2ApiService;
import com.jn.agileway.oauth2.client.exception.InvalidAccessTokenException;
import com.jn.agileway.oauth2.client.exception.OAuth2Exception;
import com.jn.agileway.oauth2.client.userinfo.OpenIdUserinfo;
import com.jn.easyjson.core.util.JSONs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class StandardOpenIdOAuth2ApiService implements OAuth2ApiService {
    private static final Logger logger = LoggerFactory.getLogger(StandardOpenIdOAuth2ApiService.class);
    private StandardOpenIdOAuth2Api api;

    private OAuth2ClientProperties oAuth2Properties;

    private OAuth2ApiResponseConverter oAuth2ApiResponseConverter;

    private IntrospectEndpointAuthTokenSupplier introspectEndpointAuthTokenSupplier;

    public StandardOpenIdOAuth2ApiService(StandardOpenIdOAuth2Api api,
                                          OAuth2ClientProperties oAuth2Properties,
                                          IntrospectEndpointAuthTokenSupplier introspectEndpointAuthTokenSupplier,
                                          OAuth2ApiResponseConverter oAuth2ApiResponseConverter
    ) {
        this.api = api;
        this.oAuth2Properties = oAuth2Properties;
        this.introspectEndpointAuthTokenSupplier = introspectEndpointAuthTokenSupplier;
        this.oAuth2ApiResponseConverter = oAuth2ApiResponseConverter;
    }

    @Override
    public OAuth2Token authorizeToken(String clientId, String clientSecret, String code, String redirectUri) {
        String basicAuthToken = AuthorizationHeaders.newBasicAuthToken(clientId, clientSecret);
        try {
            HttpResponse<Map<String, ?>> result = api.getToken(basicAuthToken,
                    GrantType.AUTHORIZATION_CODE.getName(),
                    code,
                    redirectUri,
                    clientId
            );
            logger.info("Get OAuth2 token : {}", JSONs.toJson(result.getPayload(), true, true));
            return this.oAuth2ApiResponseConverter.convertAuthorizationCodeTokenResponse(result);
        } catch (UnauthorizedException e) {
            throw new InvalidAccessTokenException("Get OAuth2 token failed, unauthorized, check your clientId, clientSecret", e);
        } catch (Exception e) {
            throw new OAuth2Exception("Get OAuth2 token failed", e);
        }
    }

    @Override
    public OAuth2Token refreshToken(String clientId, String clientSecret, String refreshToken) {
        String basicAuthToken = AuthorizationHeaders.newBasicAuthToken(clientId, clientSecret);
        try {
            HttpResponse<Map<String, ?>> result = api.refreshToken(basicAuthToken,
                    GrantType.REFRESH_TOKEN.getName(),
                    refreshToken,
                    clientId);
            logger.info("Refreshed OAuth2 token: {}", JSONs.toJson(result.getPayload(), true, true));
            return this.oAuth2ApiResponseConverter.convertRefreshTokenResponse(result);
        } catch (UnauthorizedException e) {
            throw new InvalidAccessTokenException("invalid access token", e);
        } catch (Exception e) {
            throw new OAuth2Exception("Refresh OAuth2 token failed", e);
        }
    }

    public boolean userInfoEndpointEnabled() {
        return oAuth2Properties.isUserinfoEndpointEnabled();
    }

    public boolean tokenIntrospectionEndpointEnabled() {
        return oAuth2Properties.isIntrospectEndpointEnabled();
    }

    @Override
    public OpenIdUserinfo userInfo(String accessToken) {
        if (!userInfoEndpointEnabled()) {
            throw new IllegalStateException("userinfo endpoint is disabled");
        }
        HttpResponse<Map<String, ?>> response = null;
        try {
            if (oAuth2Properties.getStandard().isUserinfoEndpointPostEnabled()) {
                response = api.getUserInfoByPost(AuthorizationHeaders.newBearerAuthToken(accessToken));
            } else {
                response = api.getUserInfoByGet(AuthorizationHeaders.newBearerAuthToken(accessToken));
            }
        } catch (UnauthorizedException e) {
            throw new InvalidAccessTokenException("Get userinfo failed, invalid access token", e);
        } catch (Exception e) {
            throw new OAuth2Exception("Get userinfo failed", e);
        }
        return this.oAuth2ApiResponseConverter.convertUserInfoResponse(response);
    }


    @Override
    public IntrospectResult introspect(String accessTokenReference, String tokenTypeHint) throws InvalidAccessTokenException {
        if (!tokenIntrospectionEndpointEnabled()) {
            throw new InvalidAccessTokenException("introspect endpoint is disabled");
        }

        HttpResponse<Map<String, ?>> response = null;
        String authToken = introspectEndpointAuthTokenSupplier.get();
        try {
            if (oAuth2Properties.getStandard().isIntrospectEndpointPostEnabled()) {
                Map<String, String> body = new HashMap<>();
                body.put(oAuth2Properties.getStandard().getIntrospectEndpointTokenParameterName(), accessTokenReference);
                response = api.introspectByPost(authToken, tokenTypeHint, body);
            } else {
                response = api.introspectByGet(authToken, oAuth2Properties.getStandard().getIntrospectEndpointTokenParameterName(), accessTokenReference, tokenTypeHint);
            }
        } catch (UnauthorizedException e) {
            throw new InvalidAccessTokenException("Introspect access-token failed, invalid access token", e);
        } catch (Exception e) {
            throw new OAuth2Exception("Introspect access-token failed", e);
        }

        return this.oAuth2ApiResponseConverter.convertIntrospectResponse(response);
    }
}
