package com.jn.agileway.oauth2.authz;

import com.jn.langx.util.function.Supplier2;
import jakarta.servlet.http.HttpServletRequest;


public interface OAuth2CallbackUriSupplier extends Supplier2<String, HttpServletRequest, String> {
    @Override
    String get(String callbackUriPath, HttpServletRequest request);
}
