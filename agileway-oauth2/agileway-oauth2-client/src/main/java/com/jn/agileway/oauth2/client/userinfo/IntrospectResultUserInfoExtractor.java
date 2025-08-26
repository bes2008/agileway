package com.jn.agileway.oauth2.client.userinfo;

import com.jn.agileway.oauth2.client.IntrospectResult;

public interface IntrospectResultUserInfoExtractor extends UserinfoExtractor<IntrospectResult, IntrospectedUserinfo> {
    IntrospectedUserinfo extract(IntrospectResult introspectResult);
}
