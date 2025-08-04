package com.jn.agileway.oauth2.authz.userinfo;

import com.jn.agileway.oauth2.authz.IntrospectResult;

public class DefaultIntrospectResultUserInfoExtractor implements IntrospectResultUserInfoExtractor {
    @Override
    public IntrospectedUserinfo extract(IntrospectResult introspectResult) {
        return new IntrospectedUserinfo(introspectResult);
    }
}
