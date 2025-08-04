package com.jn.agileway.oauth2.authz.userinfo;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#IDToken">OpenID Connect Core 1.0 #IDToken</a>
 */
public class OpenIdToken {
    private Map<String, Object> claimsSet;


    public boolean containsField(String fieldName) {
        return claimsSet.containsKey(fieldName);
    }

    public String getFieldAsString(String fieldName) {
        if (containsField(fieldName)) {
            return claimsSet.get(fieldName).toString();
        } else {
            return null;
        }
    }

    public Object getField(String fieldName) {
        return claimsSet.get(fieldName);
    }

    public OpenIdToken(Map<String, Object> claimsSet) {
        this.claimsSet = claimsSet;
    }

    public List<String> getFieldNames() {
        return new ArrayList<>(claimsSet.keySet());
    }
}
