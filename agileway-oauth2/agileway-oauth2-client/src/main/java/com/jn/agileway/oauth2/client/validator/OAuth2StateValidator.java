package com.jn.agileway.oauth2.client.validator;

public interface OAuth2StateValidator {
    boolean validate(String stateInSession, String stateInCallbackRequest);

}
