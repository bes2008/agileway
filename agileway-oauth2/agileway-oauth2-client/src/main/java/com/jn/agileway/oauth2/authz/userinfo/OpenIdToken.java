package com.jn.agileway.oauth2.authz.userinfo;

import com.nimbusds.jwt.JWTClaimsSet;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#IDToken">OpenID Connect Core 1.0 #IDToken</a>
 */
public class OpenIdToken {
    private JWTClaimsSet claimsSet;


    public boolean containsField(String fieldName) {
        return claimsSet.getClaim(fieldName) != null;
    }

    public String getFieldAsString(String fieldName) {
        try {
            return claimsSet.getStringClaim(fieldName);
        } catch (ParseException e) {
            return null;
        }
    }

    public Object getField(String fieldName) {
        return claimsSet.getClaim(fieldName);
    }

    public OpenIdToken(JWTClaimsSet claimsSet) {
        this.claimsSet = claimsSet;
    }

    public List<String> getFieldNames() {
        return new ArrayList<>(claimsSet.getClaims().keySet());
    }
}
