package com.jn.agileway.jwt.sign;

import com.jn.agileway.jwt.IllegalJWTException;
import com.jn.agileway.jwt.JWSToken;
import com.jn.agileway.jwt.Signer;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.security.crypto.mac.HMacs;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.io.Charsets;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Map;

public class HMacSigner implements Signer {
    private static Map<String,String> jwtAlgorithmToHMac;
    static{
        Map<String,String>  map  = Maps.newLinkedHashMap();
        map.put("HS256","hmacsha256");
        map.put("HS384","hmacsha384");
        map.put("HS512","hmacsha512");
        jwtAlgorithmToHMac=map;
    }

    private SecretKey secretKey;

    public HMacSigner(SecretKey secretKey){
        this.secretKey = secretKey;
    }

    @Override
    public void sign(JWSToken token) {
        String jwtSignAlgorithm = token.getHeader().getAlgorithm();
        String hmacAlgorithm= jwtAlgorithmToHMac.get(jwtSignAlgorithm);
        if(Strings.isEmpty(hmacAlgorithm)){
            throw new IllegalJWTException(StringTemplates.formatWithPlaceholder("invalid jwt sign token: unsupported algorithm: {}", jwtSignAlgorithm));
        }
        byte[] data=(token.getHeader().toBase64UrlEncoded()+"."+token.getPayload().toBase64UrlEncoded()).getBytes(Charsets.UTF_8);
        byte[] signature= HMacs.hmac(hmacAlgorithm, secretKey, data);
        token.setSignature(Base64.encodeBase64URLSafeString(signature));
    }

    @Override
    public List<String> supportedAlgorithms() {
        return Lists.newArrayList(HMacSigner.jwtAlgorithmToHMac.keySet());
    }
}

