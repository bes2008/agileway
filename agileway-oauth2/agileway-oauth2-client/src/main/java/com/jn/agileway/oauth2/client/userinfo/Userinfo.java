package com.jn.agileway.oauth2.client.userinfo;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;

public interface Userinfo {
    @NonNull
    String getUsername();

    @Nullable
    String getPhoneNumber();

    String getEmail();
}
