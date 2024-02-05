package com.jn.agileway.jwt;

import com.jn.easyjson.core.util.JSONs;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;

import java.text.ParseException;
import java.util.Map;

public class DefaultJWTParser implements JWTParser{
    @Override
    public JWT parse(String jwtstring) {
        String s = jwtstring;
        final int firstDotPos = s.indexOf(".");

        if (firstDotPos == -1) {
            throw new JWTException("Invalid JWT serialization: Missing dot delimiter(s)");
        }
        String headerBase64Url = s.substring(0, firstDotPos);

        Map<String, Object> headerJsonObject = JSONs.parse(headerBase64Url,Map.class);

        return null;
    }
}
