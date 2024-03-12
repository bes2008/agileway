package com.jn.agileway.jwt.tests.nacos_jwt;

import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.util.Strings;

import javax.crypto.Mac;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * SignAlgorithm.
 *
 * @author Weizhan▪Yun
 * @date 2023/1/15 16:42
 */
public final class NacosSignatureAlgorithm {

    private static final String JWT_SEPERATOR = ".";

    private static final int HEADER_POSITION = 0;

    private static final int PAYLOAD_POSITION = 1;

    private static final int SIGNATURE_POSITION = 2;

    private static final int JWT_PARTS = 3;

    private static final String HS256_JWT_HEADER = "eyJhbGciOiJIUzI1NiJ9";

    private static final String HS384_JWT_HEADER = "eyJhbGciOiJIUzM4NCJ9";

    private static final String HS512_JWT_HEADER = "eyJhbGciOiJIUzUxMiJ9";


    private static final Map<String, NacosSignatureAlgorithm> MAP = new HashMap<>(4);

    public static final NacosSignatureAlgorithm HS256 = new NacosSignatureAlgorithm("HS256", "HmacSHA256",
            HS256_JWT_HEADER);

    public static final NacosSignatureAlgorithm HS384 = new NacosSignatureAlgorithm("HS384", "HmacSHA384",
            HS384_JWT_HEADER);

    public static final NacosSignatureAlgorithm HS512 = new NacosSignatureAlgorithm("HS512", "HmacSHA512",
            HS512_JWT_HEADER);

    private final String algorithm;

    private final String jcaName;

    private final String header;

    static {
        MAP.put(HS256_JWT_HEADER, HS256);
        MAP.put(HS384_JWT_HEADER, HS384);
        MAP.put(HS512_JWT_HEADER, HS512);
    }

    /**
     * verify jwt.
     *
     * @param jwt complete jwt string
     * @param key for signature
     * @return object for payload
     */
    public static NacosUser verify(String jwt, Key key)  {
        if (Strings.isBlank(jwt)) {
            throw new RuntimeException("user not found!");
        }
        String[] split = jwt.split("\\.");
        if (split.length != JWT_PARTS) {
            throw new RuntimeException("token invalid!");
        }
        String header = split[HEADER_POSITION];
        String payload = split[PAYLOAD_POSITION];
        String signature = split[SIGNATURE_POSITION];

        NacosSignatureAlgorithm signatureAlgorithm = MAP.get(header);
        if (signatureAlgorithm == null) {
            throw new RuntimeException("unsupported signature algorithm");
        }
        NacosUser user = signatureAlgorithm.verify(header, payload, signature, key);
        user.setToken(jwt);
        return user;
    }

    /**
     * verify jwt.
     *
     * @param header    header of jwt
     * @param payload   payload of jwt
     * @param signature signature of jwt
     * @param key       for signature
     * @return object for payload
     */
    public NacosUser verify(String header, String payload, String signature, Key key)  {
        Mac macInstance = getMacInstance(key);
        byte[] bytes = macInstance.doFinal((header + JWT_SEPERATOR + payload).getBytes(StandardCharsets.US_ASCII));
        if (!Base64.encodeBase64URLSafeString(bytes).equals(signature)) {
            throw new RuntimeException("Invalid signature");
        }
        NacosJwtPayload nacosJwtPayload = JSONs.parse(new ByteArrayInputStream(Base64.decodeBase64(payload)), NacosJwtPayload.class);
        if (nacosJwtPayload.getExp() >= TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())) {
            return new NacosUser(nacosJwtPayload.getSub());
        }

        throw new RuntimeException("token expired!");
    }

    /**
     * get jwt expire time in seconds.
     *
     * @param jwt complete jwt string
     * @param key for signature
     * @return expire time in seconds
     */
    public static long getExpiredTimeInSeconds(String jwt, Key key)  {
        if (Strings.isBlank(jwt)) {
            throw new RuntimeException("user not found!");
        }
        String[] split = jwt.split("\\.");
        if (split.length != JWT_PARTS) {
            throw new RuntimeException("token invalid!");
        }
        String header = split[HEADER_POSITION];
        String payload = split[PAYLOAD_POSITION];
        String signature = split[SIGNATURE_POSITION];

        NacosSignatureAlgorithm signatureAlgorithm = MAP.get(header);
        if (signatureAlgorithm == null) {
            throw new RuntimeException("unsupported signature algorithm");
        }
        return signatureAlgorithm.getExpireTimeInSeconds(header, payload, signature, key);
    }

    /**
     * get jwt expire time in seconds.
     *
     * @param header    header of jwt
     * @param payload   payload of jwt
     * @param signature signature of jwt
     * @param key       for signature
     * @return expire time in seconds
     */
    public long getExpireTimeInSeconds(String header, String payload, String signature, Key key)
            throws RuntimeException {
        Mac macInstance = getMacInstance(key);
        byte[] bytes = macInstance.doFinal((header + JWT_SEPERATOR + payload).getBytes(StandardCharsets.US_ASCII));
        if (!Base64.encodeBase64URLSafe(bytes).equals(signature)) {
            throw new RuntimeException("Invalid signature");
        }
        NacosJwtPayload nacosJwtPayload = JSONs.parse(new ByteArrayInputStream(Base64.decodeBase64(payload)), NacosJwtPayload.class);
        return nacosJwtPayload.getExp();
    }

    private NacosSignatureAlgorithm(String alg, String jcaName, String header) {
        this.algorithm = alg;
        this.jcaName = jcaName;
        this.header = header;
    }

    String sign(NacosJwtPayload nacosJwtPayload, Key key) {
        String jwtWithoutSign = header + JWT_SEPERATOR + Base64.encodeBase64URLSafe(
                nacosJwtPayload.toString().getBytes(StandardCharsets.UTF_8));
        Mac macInstance = getMacInstance(key);
        byte[] bytes = jwtWithoutSign.getBytes(StandardCharsets.US_ASCII);
        String signature = Base64.encodeBase64URLSafeString(macInstance.doFinal(bytes));
        return jwtWithoutSign + JWT_SEPERATOR + signature;
    }

    private Mac getMacInstance(Key key) {
        try {
            Mac instance = Mac.getInstance(jcaName);
            instance.init(key);
            return instance;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalArgumentException("Invalid key: " + key);
        }
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getJcaName() {
        return jcaName;
    }

    public String getHeader() {
        return header;
    }
}