package com.jn.agileway.oauth2.client.userinfo;

import com.jn.agileway.jwt.JWT;
import com.jn.agileway.jwt.JWTException;
import com.jn.agileway.jwt.JWTs;
import com.jn.agileway.jwt.jwe.JWEToken;
import com.jn.agileway.oauth2.client.exception.OAuth2Exception;

import java.util.Map;

public class JwsOpenIdTokenParser implements OpenIdTokenParser {
    @Override
    public OpenIdToken parse(String idTokenString) {
        try {
            JWT jwt = JWTs.parse(idTokenString);
            if (jwt instanceof JWEToken) {
                // TODO 解密
                return null;
            }

            Map<String, Object> claimsSet = jwt.getPayload().getAllClaims();
            return new OpenIdToken(claimsSet);
        } catch (JWTException e) {
            throw new OAuth2Exception("malformed id token");
        }
    }
}
