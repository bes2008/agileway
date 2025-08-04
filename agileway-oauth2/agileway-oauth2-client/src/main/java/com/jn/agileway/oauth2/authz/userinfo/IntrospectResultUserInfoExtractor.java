package com.jn.agileway.oauth2.authz.userinfo;

import com.jn.agileway.oauth2.authz.IntrospectResult;

public interface IntrospectResultUserInfoExtractor extends UserinfoExtractor<IntrospectResult, IntrospectedUserinfo> {
    IntrospectedUserinfo extract(IntrospectResult introspectResult);
}
