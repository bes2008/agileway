package com.jn.agileway.oauth2.client.api.std;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.oauth2.client.IntrospectResult;
import com.jn.agileway.oauth2.client.OAuth2Token;
import com.jn.agileway.oauth2.client.api.OAuth2ApiResponseConverter;
import com.jn.agileway.oauth2.client.exception.OAuth2ErrorResponseException;
import com.jn.agileway.oauth2.client.exception.OAuth2Exception;
import com.jn.agileway.oauth2.client.userinfo.OpenIdUserinfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StandardOpenIdOAuth2ApiResponseConverter implements OAuth2ApiResponseConverter {
    /**
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-4.1.4">Access Token Response</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-5.1">Successful Response</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-5.2">Error Response</a>
     */
    @Override
    public OAuth2Token convertAuthorizationCodeTokenResponse(HttpResponse<Map<String, ?>> response) throws OAuth2Exception {
        return convertTokenResponseInternal(response, false);
    }

    @Override
    public OAuth2Token convertRefreshTokenResponse(HttpResponse<Map<String, ?>> response) throws OAuth2Exception {
        return convertTokenResponseInternal(response, true);
    }


    protected OAuth2Token convertTokenResponseInternal(HttpResponse<Map<String, ?>> response, boolean refresh) throws OAuth2Exception {
        boolean success = response.getStatusCode() == 200;
        Map<String, ?> payload = response.getPayload();
        if (success) {
            if (!payload.containsKey("access_token")) {
                throw new OAuth2Exception("Illegal " + (refresh ? "refresh" : "get") + " OAuth2 token response, `access_token` field is missing");
            }

            if (!payload.containsKey("token_type")) {
                throw new OAuth2Exception("Illegal" + (refresh ? "refresh" : "get") + " OAuth2 token response, `token_type` field is missing");
            }

            OAuth2Token oAuth2Token = new OAuth2Token();
            oAuth2Token.setAccessToken(payload.get("access_token").toString());
            oAuth2Token.setTokenType(payload.get("token_type").toString());
            if (payload.containsKey("expires_in")) {
                oAuth2Token.setExpiresIn(Long.parseLong(payload.get("expires_in").toString()));
            }
            if (payload.containsKey("refresh_token")) {
                oAuth2Token.setRefreshToken(payload.get("refresh_token").toString());
            }
            if (payload.containsKey("scope")) {
                oAuth2Token.setScope(payload.get("scope").toString());
            }
            if (payload.containsKey("id_token")) {
                oAuth2Token.setIdToken(payload.get("id_token").toString());
            }

            return oAuth2Token;
        }

        if (response.getStatusCode() >= 400) {
            if (!payload.containsKey("error")) {
                throw new OAuth2Exception("Illegal" + (refresh ? "refresh" : "get") + "OAuth2 token error response, `error` field is missing");
            }
            String error = payload.get("error").toString();
            String error_description = payload.containsKey("error_description") ? payload.get("error_description").toString() : null;
            String error_uri = payload.containsKey("error_uri") ? payload.get("error_uri").toString() : null;

            OAuth2ErrorResponseException exception = new OAuth2ErrorResponseException("Got access-token failed", error, error_description, error_uri);
            throw exception;
        }
        throw new RuntimeException("Error occur when " + (refresh ? "refresh" : "get") + " OAuth2 token: status-code" + response.getStatusCode() + ", error: " + response.getPayload());
    }

    private static final Set<String> STANDARD_INTROSPECT_RESPONSE_FIELDS = new HashSet<>(Arrays.asList(
            "active", "scope", "client_id", "username", "token_type", "exp", "iat", "nbf", "subject", "aud", "iss", "jti"
    ));

    /**
     * @see <a href="https://tools.ietf.org/html/rfc7662#section-2.2">RFC7662</a>
     */
    @Override
    public IntrospectResult convertIntrospectResponse(HttpResponse<Map<String, ?>> response) throws OAuth2Exception {
        int statusCode = response.getStatusCode();


        if (statusCode >= 400) {
            throw new OAuth2ErrorResponseException("Error occur when introspect OAuth2 access token, status-code: " + statusCode + "error: " + response.getPayload());
        }

        boolean success = statusCode == 200;
        Map<String, ?> payload = response.getPayload();

        if (success) {
            if (!payload.containsKey("active")) {
                throw new OAuth2Exception("Illegal OAuth2 introspect response, `active` field is missing");
            }

            boolean active = Boolean.parseBoolean(payload.get("active").toString());
            IntrospectResult result = new IntrospectResult();
            result.setActive(active);

            if (payload.containsKey("scope")) {
                result.setScope(Arrays.asList(payload.get("scope").toString().split(" ")));
            }
            if (payload.containsKey("client_id")) {
                result.setClientId(payload.get("client_id").toString());
            }
            if (payload.containsKey("username")) {
                result.setUsername(payload.get("username").toString());
            }
            if (payload.containsKey("token_type")) {
                result.setTokenType(payload.get("token_type").toString());
            }

            if (payload.containsKey("exp")) {
                result.setExp(Long.parseLong(payload.get("exp").toString()));
            }
            if (payload.containsKey("iat")) {
                result.setIat(Long.parseLong(payload.get("iat").toString()));
            }
            if (payload.containsKey("nbf")) {
                result.setNbf(Long.parseLong(payload.get("nbf").toString()));
            }
            if (payload.containsKey("subject")) {
                result.setSubject(payload.get("subject").toString());
            }
            if (payload.containsKey("aud")) {
                result.setAud(payload.get("aud").toString());
            }
            if (payload.containsKey("iss")) {
                result.setIss(payload.get("iss").toString());
            }
            if (payload.containsKey("jti")) {
                result.setJti(payload.get("jti").toString());
            }

            for (String key : payload.keySet()) {
                if (!STANDARD_INTROSPECT_RESPONSE_FIELDS.contains(key)) {
                    result.getExtensionFields().put(key, payload.get(key));
                }
            }

            return result;
        } else {
            throw new IllegalStateException();
        }

    }


    /**
     * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#UserInfo">UserInfo</a>
     */
    @Override
    public OpenIdUserinfo convertUserInfoResponse(HttpResponse<Map<String, ?>> response) throws OAuth2Exception {
        int statusCode = response.getStatusCode();
        if (statusCode == 200) {
            return new OpenIdUserinfo(response.getPayload());
        }
        throw new OAuth2Exception("Error occur when get userinfo, status-code: " + statusCode + ", error: " + response.getPayload());
    }

}
