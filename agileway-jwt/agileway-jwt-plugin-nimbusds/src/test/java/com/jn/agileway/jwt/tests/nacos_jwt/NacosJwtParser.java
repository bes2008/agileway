package com.jn.agileway.jwt.tests.nacos_jwt;



import com.jn.langx.codec.base64.Base64;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.concurrent.TimeUnit;

/**
 * JwtParse.
 *
 */
public class NacosJwtParser {

    private final NacosSignatureAlgorithm signatureAlgorithm;

    private final Key key;

    public NacosJwtParser(String base64edKey) {
        byte[] decode;
        try {
            decode = Base64.decodeBase64(base64edKey);
        } catch (IllegalArgumentException e) {
            decode = base64edKey.getBytes(StandardCharsets.US_ASCII);
        }

        int bitLength = decode.length << 3;
        if (bitLength < 256) {
            String msg = "The specified key byte array is " + bitLength + " bits which "
                    + "is not secure enough for any JWT HMAC-SHA algorithm.  The JWT "
                    + "JWA Specification (RFC 7518, Section 3.2) states that keys used with HMAC-SHA algorithms MUST have a "
                    + "size >= 256 bits (the key size must be greater than or equal to the hash "
                    + "output size).  See https://tools.ietf.org/html/rfc7518#section-3.2 for more information.";
            throw new IllegalArgumentException(msg);
        }

        if (bitLength < 384) {
            this.signatureAlgorithm = NacosSignatureAlgorithm.HS256;
        } else if (bitLength < 512) {
            this.signatureAlgorithm = NacosSignatureAlgorithm.HS384;
        } else {
            this.signatureAlgorithm = NacosSignatureAlgorithm.HS512;
        }
        this.key = new SecretKeySpec(decode, signatureAlgorithm.getJcaName());
    }

    private String sign(NacosJwtPayload payload) {
        return signatureAlgorithm.sign(payload, key);
    }

    public JwtBuilder jwtBuilder() {
        return new JwtBuilder();
    }

    public NacosUser parse(String token)  {
        return NacosSignatureAlgorithm.verify(token, key);
    }

    public long getExpireTimeInSeconds(String token)  {
        return NacosSignatureAlgorithm.getExpiredTimeInSeconds(token, key);
    }

    public class JwtBuilder {

        private final NacosJwtPayload nacosJwtPayload = new NacosJwtPayload();

        public JwtBuilder setUserName(String userName) {
            this.nacosJwtPayload.setSub(userName);
            return this;
        }

        public JwtBuilder setExpiredTime(long validSeconds) {
            this.nacosJwtPayload.setExp(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + validSeconds);
            return this;
        }

        public String compact() {
            return sign(nacosJwtPayload);
        }
    }
}