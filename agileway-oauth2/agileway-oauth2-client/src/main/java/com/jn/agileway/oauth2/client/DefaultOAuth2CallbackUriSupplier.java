package com.jn.agileway.oauth2.client;

import com.jn.langx.util.Strings;
import jakarta.servlet.http.HttpServletRequest;

public class DefaultOAuth2CallbackUriSupplier implements OAuth2CallbackUriSupplier {
    @Override
    public String get(String callbackPath, HttpServletRequest request) {
        String callbackUri = request.getScheme() + "://" + request.getLocalAddr() + ":" + request.getLocalPort()
                + request.getContextPath()
                + (Strings.startsWith(callbackPath, "/") ? callbackPath : ("/" + callbackPath));
        return callbackUri;
    }
}
