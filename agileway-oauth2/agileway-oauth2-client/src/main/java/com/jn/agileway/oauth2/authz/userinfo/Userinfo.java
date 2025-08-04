package com.jn.agileway.oauth2.authz.userinfo;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;

public interface Userinfo {
    @NonNull
    String getUsername();

    @Nullable
    String getPhoneNumber();

    String getEmail();
}
