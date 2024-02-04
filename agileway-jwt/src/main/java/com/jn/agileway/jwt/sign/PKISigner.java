package com.jn.agileway.jwt.sign;

import com.jn.agileway.jwt.IllegalJWTException;
import com.jn.agileway.jwt.JWSToken;
import com.jn.agileway.jwt.Signer;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.security.Securitys;
import com.jn.langx.security.crypto.signature.Signatures;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.io.Charsets;

import java.security.PrivateKey;
import java.security.Signature;
import java.util.List;
import java.util.Map;

public class PKISigner implements Signer {

    private static Map<String,String> jwtAlgorithmToPKI;
    static{
        Map<String,String>  map  = Maps.newLinkedHashMap();
        map.put("RS256","SHA256withRSA");
        map.put("RS384","SHA384withRSA");
        map.put("RS512","SHA512withRSA");

        map.put("PS256","SHA256withRSAandMGF1");
        map.put("PS384","SHA384withRSAandMGF1");
        map.put("PS512","SHA512withRSAandMGF1");
        jwtAlgorithmToPKI=map;
    }

    private final PrivateKey privateKey;

    public PKISigner(PrivateKey privateKey){
        this.privateKey = privateKey;
    }

    @Override
    public void sign(JWSToken token) {
        String jwtSignAlgorithm = token.getHeader().getAlgorithm();
        String pkiAlgorithm = jwtAlgorithmToPKI.get(jwtSignAlgorithm);

        if(Strings.isEmpty(pkiAlgorithm)){
            throw new IllegalJWTException(StringTemplates.formatWithPlaceholder("invalid jwt sign token: unsupported algorithm: {}", jwtSignAlgorithm));
        }

        byte[] data=(token.getHeader().toBase64UrlEncoded()+"."+token.getPayload().toBase64UrlEncoded()).getBytes(Charsets.UTF_8);

        Signature signer= Signatures.createSignature(pkiAlgorithm,null, privateKey, Securitys.getSecureRandom());
        byte[] signature= Signatures.sign(signer, data);

        token.setSignature(Base64.encodeBase64URLSafeString(signature));
    }

    @Override
    public List<String> supportedAlgorithms() {
        return Lists.newArrayList(PKISigner.jwtAlgorithmToPKI.keySet());
    }
}
