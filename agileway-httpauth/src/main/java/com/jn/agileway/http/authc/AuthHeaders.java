package com.jn.agileway.http.authc;

import com.jn.langx.util.Preconditions;

import java.util.Map;

public class AuthHeaders {
    public static WwwAuthenticate parseWwwAuthenticate(String header) {
        Preconditions.checkNotEmpty(header, "WWW-Authenticate header is empty");

        WwwAuthenticate wwwAuthenticate = new WwwAuthenticate();
        // 1. 分割认证方案和参数部分（如：Bearer realm="example"）
        String[] parts = header.split("\\s+", 2);

        String authScheme = parts[0];
        wwwAuthenticate.setAuthScheme(authScheme);

        // 2. 解析参数键值对
        if (parts.length > 1) {
            // 处理带引号的参数值（如：realm="example", scope="read write"）
            String[] params = parts[1].split("\\s*,\\s*");
            for (String param : params) {
                String[] kv = param.split("=", 2);
                if (kv.length == 2) {
                    String value = kv[1].replaceAll("^\"|\"$", "");
                    wwwAuthenticate.setField(kv[0], value);
                }
            }
        }

        return wwwAuthenticate;
    }

    public static String buildWwwAuthenticate(WwwAuthenticate authenticate) {
        return buildAuthHeaderString(authenticate.getAuthScheme(), authenticate.getFields());
    }

    public static String buildAuthHeaderString(String authScheme, String encoded) {
        return authScheme + " " + encoded;
    }

    public static String buildAuthHeaderString(String authScheme, Map<String, String> fields) {
        StringBuilder builder = new StringBuilder();
        builder.append(authScheme);
        int i = 0;
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            builder.append(" ");
            if (i > 0) {
                builder.append(",");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            i++;
        }
        return builder.toString();
    }

}
