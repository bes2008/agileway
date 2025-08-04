package com.jn.agileway.oauth2.client.validator;


import com.jn.langx.util.Strings;

public class DefaultStateValidator implements OAuth2StateValidator {
    @Override
    public boolean validate(String stateInSession, String stateInCallbackRequest) {
        return Strings.equals(stateInSession, stateInCallbackRequest);
    }
}
