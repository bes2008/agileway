package com.jn.agileway.oauth2.authz.api;

import com.jn.agileway.oauth2.authz.IntrospectResult;
import com.jn.agileway.oauth2.authz.OAuth2Token;
import com.jn.agileway.oauth2.authz.exception.OAuth2Exception;
import com.jn.agileway.oauth2.authz.userinfo.OpenIdUserinfo;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface OAuth2ApiResponseConverter {

    OAuth2Token convertAuthorizationCodeTokenResponse(ResponseEntity<Map<String, ?>> response) throws OAuth2Exception;

    OAuth2Token convertRefreshTokenResponse(ResponseEntity<Map<String, ?>> response) throws OAuth2Exception;

    OpenIdUserinfo convertUserInfoResponse(ResponseEntity<Map<String, ?>> response) throws OAuth2Exception;

    IntrospectResult convertIntrospectResponse(ResponseEntity<Map<String, ?>> response) throws OAuth2Exception;
}
