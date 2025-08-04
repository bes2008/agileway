package com.jn.agileway.oauth2.authz.validator;

public interface OAuth2StateValidator {
    boolean validate(String stateInSession, String stateInCallbackRequest);

}
