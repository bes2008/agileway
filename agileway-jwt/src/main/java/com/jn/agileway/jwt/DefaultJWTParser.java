package com.jn.agileway.jwt;

import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.MapAccessor;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.util.Base64URL;

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

        MapAccessor headerAccessor = new MapAccessor( JSONs.<Map<String,Object>>parse(headerBase64Url,Map.class));
        String algorithm = headerAccessor.getString(JWTs.ClaimNames.Header.ALGORITHM);

        if (Strings.isBlank(algorithm)){
            throw new JWTException("invalid jwt token: missing `alg` header");
        }
        AlgorithmType algorithmType = JWTs.getJWTService().getAlgorithmType(algorithm);
        if(algorithmType==AlgorithmType.UNSUPPORTED){
            throw new JWTException("unsupported jwt algorithm: "+ algorithm);
        }
        if(algorithmType==AlgorithmType.NONE){
            return parsePlainToken(jwtstring);
        }
        if(algorithmType==AlgorithmType.JWS){
            return parseJWSToken(jwtstring);
        }
        return null;
    }

    private JWTPlainToken parsePlainToken(String jwtstring) {
        String[] parts = org.bouncycastle.util.Strings.split(jwtstring,'.');
        if(parts.length!=2){
            throw new JWTException("Invalid jwt plain token, Unexpected number of Base64URL parts, must be 2");
        }
        String headerBase64Url = parts[0];
        String payloadBase64Url = parts[1];

        String headerJson = Base64.decodeBase64ToString(headerBase64Url);
        String payloadJson = Base64.decodeBase64ToString(payloadBase64Url);

        Map<String,Object> header= JSONs.parse(headerJson,Map.class);
        Map<String,Object> payload=JSONs.parse(payloadJson, Map.class);

        return new JWTPlainToken(header, payload);
    }

    private JWSToken parseJWSToken(String jwtstring) {
        String[] parts = Strings.split(jwtstring,".");
        if(parts.length!=3){
            throw new JWTException("Invalid jwt signed token, Unexpected number of Base64URL parts, must be 3");
        }
        String headerBase64Url = parts[0];
        String payloadBase64Url = parts[1];
        String signatureBase64Url = parts[2];

        String headerJson = Base64.decodeBase64ToString(headerBase64Url);
        String payloadJson = Base64.decodeBase64ToString(payloadBase64Url);

        Map<String,Object> header= JSONs.parse(headerJson,Map.class);
        Map<String,Object> payload=JSONs.parse(payloadJson, Map.class);

        JWSToken token= new JWSToken(header, payload);
        token.setSignature(signatureBase64Url);
        return token;
    }

    private JWEToken parseJWEToken(String jwtstring){
        String[] parts = Strings.split(jwtstring,".");
        if(parts.length!=5){
            throw new JWTException("Invalid jwt encrypted token, Unexpected number of Base64URL parts, must be 5 ");
        }
        return JWTs.getJWTService().getJWEPlugin().parse(jwtstring);
    }

}
