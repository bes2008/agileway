package com.jn.agileway.oauth2.client.userinfo;


import com.jn.langx.util.Preconditions;

import java.util.Map;

public class OpenIdUserinfo implements Userinfo {
    protected Map<String, ?> userInfo;

    public OpenIdUserinfo(Map<String, ?> userInfo) {
        Preconditions.checkNotNull(userInfo);
        this.userInfo = userInfo;
    }


    public Map<String, ?> getFields() {
        return userInfo;
    }

    public String getUsername() {
        return (String) userInfo.get("sub");
    }

    public String getEmail() {
        return (String) userInfo.get("email");
    }

    public String getPhoneNumber() {
        return (String) userInfo.get("phone_number");
    }
}
