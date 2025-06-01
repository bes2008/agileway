package com.jn.agileway.httpclient.auth;

import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.regexp.Regexp;
import com.jn.langx.util.regexp.RegexpMatcher;
import com.jn.langx.util.regexp.Regexps;

import java.util.List;
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
            String fieldsString = parts[1];
            String keyValuePattern = "\\s*([a-zA-Z0-9_]+)\\s*=\\s*((\"[^\"]*\")|([^,]*))\\s*,?";
            Regexp regexp = Regexps.compile(keyValuePattern);
            RegexpMatcher matcher = regexp.matcher(fieldsString);
            while (matcher.find()) {
                String key = Strings.trim(matcher.group(1));
                String value = matcher.group(3);
                if (Strings.isBlank(value)) {
                    value = matcher.group(4);
                }
                value = Strings.trim(value);
                if (Strings.isNotBlank(value)) {
                    wwwAuthenticate.setField(key, value, WwwAuthenticate.isQuoted(value));
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
            if (i > 0) {
                builder.append(",");
            }
            builder.append(" ");
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            i++;
        }
        return builder.toString();
    }

    public static List<String> getFieldAsList(String fieldValue, String separator) {
        if (Strings.isNotBlank(fieldValue)) {
            return Pipeline.of(Strings.split(fieldValue, separator)).asList();
        }
        return Lists.immutableList();
    }

}
