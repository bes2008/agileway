package com.jn.agileway.oauth2.authz.api.std;

import com.bes.um3rd.utils.HttpHeaderUtils;
import com.jn.agileway.oauth2.authz.OAuth2Properties;
import com.jn.agileway.oauth2.authz.api.IntrospectEndpointAuthTokenSupplier;

public class BasicIntrospectEndpointAuthTokenSupplier implements IntrospectEndpointAuthTokenSupplier {
    private OAuth2Properties oauth2Properties;

    public BasicIntrospectEndpointAuthTokenSupplier(OAuth2Properties oauth2Properties) {
        this.oauth2Properties = oauth2Properties;
    }

    @Override
    public String get() {
        String basicAuthToken = HttpHeaderUtils.newBasicAuthToken(oauth2Properties.getClientId(), oauth2Properties.getClientSecret());
        return basicAuthToken;
    }
}
