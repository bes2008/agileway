package com.jn.agileway.jwt;

import com.jn.langx.codec.base64.Base64;
import com.jn.langx.security.crypto.mac.HMacs;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;

import javax.crypto.SecretKey;

public class HMacVerifier implements Verifier {

    private SecretKey secretKey;

    public HMacVerifier(SecretKey secretKey) {
        if (secretKey.getEncoded() != null && secretKey.getEncoded().length < 256 / 8) {
            throw new JWTException("The secret length must be at least 256 bits");
        }
        this.secretKey = secretKey;
    }

    @Override
    public boolean verify(JWSToken token) {
        String jwtSignAlgorithm = token.getHeader().getAlgorithm();
        String hmacAlgorithm = Signs.getJcaHMacAlgorithm(jwtSignAlgorithm);
        if (Strings.isEmpty(hmacAlgorithm)) {
            throw new JWTException(StringTemplates.formatWithPlaceholder("invalid jwt sign token: unsupported algorithm: {}", jwtSignAlgorithm));
        }
        byte[] data = (token.getHeader().toBase64UrlEncoded() + "." + token.getPayload().toBase64UrlEncoded()).getBytes(Charsets.UTF_8);
        byte[] signature = HMacs.hmac(hmacAlgorithm, secretKey, data);
        String actualSignature = Base64.encodeBase64URLSafeString(signature);
        String expectedSignature = token.getSignature();
        return Objs.equals(actualSignature, expectedSignature);
    }

}
