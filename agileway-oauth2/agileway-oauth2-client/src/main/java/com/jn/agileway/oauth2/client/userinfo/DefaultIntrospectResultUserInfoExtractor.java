package com.jn.agileway.oauth2.client.userinfo;

import com.jn.agileway.oauth2.client.IntrospectResult;

public class DefaultIntrospectResultUserInfoExtractor implements IntrospectResultUserInfoExtractor {
    @Override
    public IntrospectedUserinfo extract(IntrospectResult introspectResult) {
        return new IntrospectedUserinfo(introspectResult);
    }
}
