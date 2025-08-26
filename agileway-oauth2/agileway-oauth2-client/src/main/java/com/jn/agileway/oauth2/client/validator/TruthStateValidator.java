package com.jn.agileway.oauth2.client.validator;

public class TruthStateValidator implements OAuth2StateValidator {
    @Override
    public boolean validate(String stateInSession, String stateInCallbackRequest) {
        return true;
    }
}
