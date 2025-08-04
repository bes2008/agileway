package com.jn.agileway.oauth2.authz.validator;


import com.jn.langx.util.Strings;

public class NonEmptyStateValidator implements OAuth2StateValidator {
    public boolean validate(String stateInSession, String stateInCallbackRequest) {
        return Strings.isNotBlank(stateInCallbackRequest);
    }
}
