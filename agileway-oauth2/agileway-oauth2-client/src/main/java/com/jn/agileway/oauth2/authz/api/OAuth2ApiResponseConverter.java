package com.jn.agileway.oauth2.authz.api;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.oauth2.authz.IntrospectResult;
import com.jn.agileway.oauth2.authz.OAuth2Token;
import com.jn.agileway.oauth2.authz.exception.OAuth2Exception;
import com.jn.agileway.oauth2.authz.userinfo.OpenIdUserinfo;

import java.util.Map;

public interface OAuth2ApiResponseConverter {

    OAuth2Token convertAuthorizationCodeTokenResponse(HttpResponse<Map<String, ?>> response) throws OAuth2Exception;

    OAuth2Token convertRefreshTokenResponse(HttpResponse<Map<String, ?>> response) throws OAuth2Exception;

    OpenIdUserinfo convertUserInfoResponse(HttpResponse<Map<String, ?>> response) throws OAuth2Exception;

    IntrospectResult convertIntrospectResponse(HttpResponse<Map<String, ?>> response) throws OAuth2Exception;
}
