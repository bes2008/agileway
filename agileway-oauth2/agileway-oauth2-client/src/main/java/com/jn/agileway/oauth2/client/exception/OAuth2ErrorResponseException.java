package com.jn.agileway.oauth2.client.exception;


import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;

public class OAuth2ErrorResponseException extends OAuth2Exception {
    @NonNull
    private String error;
    @Nullable
    private String errorDescription;
    @Nullable
    private String errorUri;

    public OAuth2ErrorResponseException(@NonNull String errorMessage, String error, String errorDescription, String errorUri) {
        super(errorMessage + " ; error: " + error + " , error_description: " + errorDescription + " , error_uri: " + errorUri);
        this.error = error;
        this.errorDescription = errorDescription;
        this.errorUri = errorUri;
    }

    public OAuth2ErrorResponseException(@NonNull String errorMessage) {
        super(errorMessage);
    }

    @NonNull
    public String getError() {
        return error;
    }

    @Nullable
    public String getErrorDescription() {
        return errorDescription;
    }

    @Nullable
    public String getErrorUri() {
        return errorUri;
    }
}
