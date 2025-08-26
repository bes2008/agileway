package com.jn.agileway.oauth2.client.api.std;


import com.jn.agileway.httpclient.auth.AuthScheme;

public class StandardOpenOAuth2Properties {

    private boolean userinfoEndpointPostEnabled = true;

    private boolean introspectEndpointPostEnabled = true;

    private String introspectEndpointTokenParameterName = "token";

    private AuthScheme introspectEndpointAuthType = AuthScheme.BASIC;

    private Class<? extends StandardOpenIdOAuth2Api> apiInterface = StandardOpenIdOAuth2Api.class;

    public boolean isUserinfoEndpointPostEnabled() {
        return userinfoEndpointPostEnabled;
    }

    public void setUserinfoEndpointPostEnabled(boolean userinfoEndpointPostEnabled) {
        this.userinfoEndpointPostEnabled = userinfoEndpointPostEnabled;
    }


    public boolean isIntrospectEndpointPostEnabled() {
        return introspectEndpointPostEnabled;
    }

    public void setIntrospectEndpointPostEnabled(boolean introspectEndpointPostEnabled) {
        this.introspectEndpointPostEnabled = introspectEndpointPostEnabled;
    }

    public String getIntrospectEndpointTokenParameterName() {
        return introspectEndpointTokenParameterName;
    }

    public void setIntrospectEndpointTokenParameterName(String introspectEndpointTokenParameterName) {
        this.introspectEndpointTokenParameterName = introspectEndpointTokenParameterName;
    }

    public Class<? extends StandardOpenIdOAuth2Api> getApiInterface() {
        return apiInterface;
    }

    public void setApiInterface(Class<? extends StandardOpenIdOAuth2Api> apiInterface) {
        this.apiInterface = apiInterface;
    }

    public AuthScheme getIntrospectEndpointAuthType() {
        return introspectEndpointAuthType;
    }

    public void setIntrospectEndpointAuthType(AuthScheme introspectEndpointAuthType) {
        this.introspectEndpointAuthType = introspectEndpointAuthType;
    }
}
