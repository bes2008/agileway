package com.jn.agileway.jwt;

import com.jn.agileway.jwt.jwe.JWEToken;
import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.MapAccessor;

import java.util.Map;

class DefaultJWTParser implements JWTParser {
    @Override
    public JWT parse(String jwtstring) {
        final int firstDotPos = jwtstring.indexOf(".");

        if (firstDotPos == -1) {
            throw new JWTException("Invalid JWT serialization: Missing dot delimiter(s)");
        }
        String headerBase64Url = jwtstring.substring(0, firstDotPos);

        String headerJsonString = Base64.decodeBase64ToString(headerBase64Url);
        MapAccessor headerAccessor = new MapAccessor(JSONs.<Map<String, Object>>parse(headerJsonString, Map.class));
        String algorithm = headerAccessor.getString(JWTs.Headers.ALGORITHM);

        if (Strings.isBlank(algorithm)) {
            throw new JWTException("invalid jwt token: missing `alg` header");
        }
        AlgorithmType algorithmType = JWTs.getJWTService().getAlgorithmType(algorithm);
        if (algorithmType == AlgorithmType.UNSUPPORTED) {
            throw new JWTException("unsupported jwt algorithm: " + algorithm);
        }
        if (algorithmType == AlgorithmType.NONE) {
            return parseJWSToken(jwtstring, true);
        }
        if (algorithmType == AlgorithmType.JWS) {
            return parseJWSToken(jwtstring, false);
        }
        return parseJWEToken(jwtstring);
    }


    private JWSToken parseJWSToken(String jwtstring, boolean plainToken) {
        String[] parts = Strings.split(jwtstring, ".");
        if (parts.length != (plainToken ? 2 : 3)) {
            throw new JWTException("Invalid jwt signed token, Unexpected number of Base64URL parts, must be 3");
        }
        String headerBase64Url = parts[0];
        String payloadBase64Url = parts[1];
        JWSToken token = new JWSToken(headerBase64Url, payloadBase64Url);
        if (!plainToken) {
            String signatureBase64Url = parts[2];
            token.setSignature(signatureBase64Url);
        }
        return token;
    }

    private JWEToken parseJWEToken(String jwtstring) {
        String[] parts = Strings.split(jwtstring, ".");
        if (parts.length != 5) {
            throw new JWTException("Invalid jwt encrypted token, Unexpected number of Base64URL parts, must be 5 ");
        }
        return JWTs.getJWTService().getJWEPlugin().parse(jwtstring);
    }

}
