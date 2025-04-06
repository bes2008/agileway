package com.jn.agileway.http.authc;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;

import java.util.LinkedHashMap;
import java.util.Map;

public class WwwAuthenticate {
    private String authScheme;
    private Map<String, String> fields = new LinkedHashMap<String, String>();

    public String getAuthScheme() {
        return authScheme;
    }

    public void setAuthScheme(String authScheme) {
        this.authScheme = authScheme;
    }

    public final String getField(String fieldName) {
        return unquoted(fields.get(fieldName));
    }

    public static String unquoted(String str) {
        if (str == null) {
            return null;
        }
        if (Strings.isEmpty(str)) {
            return "";
        }
        return Strings.strip(str, "\"");
    }

    public static String quoted(String str) {
        if (Strings.isBlank(str)) {
            return "";
        }
        return StringTemplates.formatWithPlaceholder("{}", unquoted(str));
    }

    public final void setField(String fieldName, String fieldValue) {
        setField(fieldName, fieldValue, false);
    }

    public final void setField(String fieldName, String fieldValue, boolean quoted) {
        this.fields.put(fieldName, quoted ? quoted(fieldValue) : unquoted(fieldValue));
    }

    @Override
    public String toString() {
        return toHeaderValue();
    }

    public String toHeaderValue() {
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
