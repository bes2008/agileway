package com.jn.agileway.jwt.sign;

import com.jn.agileway.jwt.JWTException;
import com.jn.agileway.jwt.JWSToken;
import com.jn.agileway.jwt.Signer;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.security.crypto.mac.HMacs;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.io.Charsets;
import com.jn.agileway.jwt.JWTs;
import javax.crypto.SecretKey;
import java.util.List;

public class HMacSigner implements Signer {

    private SecretKey secretKey;

    public HMacSigner(SecretKey secretKey){
        this.secretKey = secretKey;
    }

    @Override
    public void sign(JWSToken token) {
        String jwtSignAlgorithm = token.getHeader().getAlgorithm();
        String hmacAlgorithm= Signs.JWT_TO_HMAC_ALGORITHMS.get(jwtSignAlgorithm);
        if(Strings.isEmpty(hmacAlgorithm)){
            throw new JWTException(StringTemplates.formatWithPlaceholder("invalid jwt sign token: unsupported algorithm: {}", jwtSignAlgorithm));
        }
        byte[] data=(token.getHeader().toBase64UrlEncoded()+"."+token.getPayload().toBase64UrlEncoded()).getBytes(Charsets.UTF_8);
        byte[] signature= HMacs.hmac(hmacAlgorithm, secretKey, data);
        token.setSignature(Base64.encodeBase64URLSafeString(signature));
    }

    @Override
    public List<String> supportedAlgorithms() {
        return Lists.newArrayList(Signs.JWT_TO_HMAC_ALGORITHMS.keySet());
    }
}

