package com.jn.agileway.oauth2.authz.api.std;

import com.bes.um3rd.utils.AuthcType;

public class StandardOpenOAuth2Properties {

    private boolean userinfoEndpointPostEnabled = true;

    private boolean introspectEndpointPostEnabled = true;

    private String introspectEndpointTokenParameterName = "token";

    private AuthcType introspectEndpointAuthType = AuthcType.Basic;

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

    public AuthcType getIntrospectEndpointAuthType() {
        return introspectEndpointAuthType;
    }

    public void setIntrospectEndpointAuthType(AuthcType introspectEndpointAuthType) {
        this.introspectEndpointAuthType = introspectEndpointAuthType;
    }
}
