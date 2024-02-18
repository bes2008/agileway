package com.jn.agileway.jwt;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.spi.CommonServiceProvider;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.util.Map;

/**
 * 生成 JWT string
 * JWT => JsonTreeNode => byte[] => enc => Base64URL parts => sign => merge =>  String
 * 解析 JWT string
 * String => split  => verify_sign => Base64URL parts = dec  => byte[] => JsonTreeNode => JWT
 */
public class JWTs {

    public static final String JWT_ALGORITHM_PLAIN = "none";
    public static final String JWT_TYPE_DEFAULT = "JWT";


    public static final class Headers {

        ////////////////////////////////////////////////////////////////////////////////
        // Generic JWS and JWE Header Parameters
        ////////////////////////////////////////////////////////////////////////////////


        /**
         * Used in JWS Header and JWE Header.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7515#section-4.1.1">RFC 7515 "alg" (JWS Algorithm) Header Parameter</a>
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7516#section-4.1.1">RFC 7516 "alg" (JWE Algorithm) Header Parameter</a>
         */
        public static final String ALGORITHM = "alg";


        /**
         * Used in JWE Header.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7516#section-4.1.2">RFC 7516 "enc" (Encryption Algorithm) Header Parameter</a>
         */
        public static final String ENCRYPTION_ALGORITHM = "enc";


        /**
         * Used in JWE Header.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7516#section-4.1.3">RFC 7516 "zip" (Compression Algorithm) Header Parameter</a>
         */
        public static final String COMPRESSION_ALGORITHM = "zip";


        /**
         * Used in JWS Header and JWE Header.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7515#section-4.1.2">RFC 7515 "jku" (JWK Set URL) Header Parameter</a>
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7516#section-4.1.4">RFC 7516 "jku" (JWK Set URL) Header Parameter</a>
         */
        public static final String JWK_SET_URL = "jku";


        /**
         * Used in JWS Header and JWE Header.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7515#section-4.1.3">RFC 7515 "jwk" (JSON Web Key) Header Parameter</a>
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7516#section-4.1.5">RFC 7516 "jwk" (JSON Web Key) Header Parameter</a>
         */
        public static final String JWK = "jwk";


        /**
         * Used in JWS Header and JWE Header.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7515#section-4.1.4">RFC 7515 "kid" (Key ID) Header Parameter</a>
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7516#section-4.1.6">RFC 7516 "kid" (Key ID) Header Parameter</a>
         */
        public static final String KEY_ID = "kid";


        /**
         * Used in JWS Header and JWE Header.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7515#section-4.1.5">RFC 7515 "x5u" (X.509 Certificate URL) Header Parameter</a>
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7516#section-4.1.7">RFC 7516 "x5u" (X.509 Certificate URL) Header Parameter</a>
         */
        public static final String X_509_CERT_URL = "x5u";


        /**
         * Used in JWS Header and JWE Header.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7515#section-4.1.6">RFC 7515 "x5c" (X.509 Certificate Chain) Header Parameter</a>
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7516#section-4.1.8">RFC 7516 "x5c" (X.509 Certificate Chain) Header Parameter</a>
         */
        public static final String X_509_CERT_CHAIN = "x5c";


        /**
         * Used in JWS Header and JWE Header.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7515#section-4.1.7">RFC 7515 "x5t" (X.509 Certificate SHA-1 Thumbprint) Header Parameter</a>
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7516#section-4.1.9">RFC 7516 "x5t" (X.509 Certificate SHA-1 Thumbprint) Header Parameter</a>
         */
        public static final String X_509_CERT_SHA_1_THUMBPRINT = "x5t";


        /**
         * Used in JWS Header and JWE Header.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7515#section-4.1.8">RFC 7515 "x5t#S256" (X.509 Certificate SHA-256 Thumbprint) Header Parameter</a>
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7516#section-4.1.10">RFC 7516 "x5t#S256" (X.509 Certificate SHA-256 Thumbprint) Header Parameter</a>
         */
        public static final String X_509_CERT_SHA_256_THUMBPRINT = "x5t#S256";


        /**
         * Used in JWS Header and JWE Header.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7515#section-4.1.9">RFC 7515 "typ" (Type) Header Parameter</a>
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7516#section-4.1.11">RFC 7516 "typ" (Type) Header Parameter</a>
         */
        public static final String TYPE = "typ";


        /**
         * Used in JWS Header and JWE Header.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7515#section-4.1.10">RFC 7515 "cty" (Content Type) Header Parameter</a>
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7516#section-4.1.12">RFC 7516 "cty" (Content Type) Header Parameter</a>
         */
        public static final String CONTENT_TYPE = "cty";


        /**
         * Used in JWS Header and JWE Header.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7515#section-4.1.11">RFC 7515 "crit" (Critical) Header Parameter</a>
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7516#section-4.1.13">RFC 7516 "crit" (Critical) Header Parameter</a>
         */
        public static final String CRITICAL = "crit";


        ////////////////////////////////////////////////////////////////////////////////
        // Algorithm-Specific Header Parameters
        ////////////////////////////////////////////////////////////////////////////////


        /**
         * Used in JWE Header with ECDH key agreement.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.1.1">RFC 7518 "epk" (Ephemeral Public Key) Header Parameter</a>
         */
        public static final String EPHEMERAL_PUBLIC_KEY = "epk";


        /**
         * Used in JWE Header with ECDH key agreement.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.1.2">RFC 7518 "apu" (Agreement PartyUInfo) Header Parameter</a>
         */
        public static final String AGREEMENT_PARTY_U_INFO = "apu";


        /**
         * Used in JWE Header with ECDH key agreement.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.1.3">RFC 7518 "apv" (Agreement PartyVInfo) Header Parameter</a>
         */
        public static final String AGREEMENT_PARTY_V_INFO = "apv";


        /**
         * Used in JWE Header with AES GCN key encryption.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7.1.1">RFC 7518 "iv" (Initialization Vector) Header Parameter</a>
         */
        public static final String INITIALIZATION_VECTOR = "iv";


        /**
         * Used in JWE Header with AES GCN key encryption.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7.1.2">RFC 7518 "tag" (Authentication Tag) Header Parameter</a>
         */
        public static final String AUTHENTICATION_TAG = "tag";


        /**
         * Used in JWE Header with PBES2 key encryption.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.8.1.1">RFC 7518 "p2s" (PBES2 Salt Input) Header Parameter</a>
         */
        public static final String PBES2_SALT_INPUT = "p2s";


        /**
         * Used in JWE Header with PBES2 key encryption.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.8.1.2">RFC 7518 "p2c" (PBES2 Count) Header Parameter</a>
         */
        public static final String PBES2_COUNT = "p2c";


        /**
         * Used in JWE Header with ECDH-1PU key agreement.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/draft-madden-jose-ecdh-1pu-04#section-2.2.1">"skid" Header Parameter</a>
         */
        public static final String SENDER_KEY_ID = "skid";


        ////////////////////////////////////////////////////////////////////////////////
        // RFC 7797 (JWS Unencoded Payload Option) Header Parameters
        ////////////////////////////////////////////////////////////////////////////////


        /**
         * Used in JWS Header with unencoded {@link Payload}.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc7797#section-3">RFC 7797 "b64" (base64url-encode payload) Header Parameter</a>
         */
        public static final String BASE64_URL_ENCODE_PAYLOAD = "b64";

    }

    public static class JWSAlgorithms {
        // hmac 相关,使用的是 secret key
        public static final String HS256 = "HS256";
        public static final String HS384 = "HS384";
        public static final String HS512 = "HS512";

        // 下面的是一些 private key 进行签名的算法
        // rsa 相关

        // SHA256withRSA
        public static final String RS256 = "RS256";
        // SHA384withRSA
        public static final String RS384 = "RS384";
        // SHA512withRSA
        public static final String RS512 = "RS512";

        // RSASSA-PSS, SHA-256
        public static final String PS256 = "PS256";

        // RSASSA-PSS, SHA-384
        public static final String PS384 = "PS384";

        // RSASSA-PSS, SHA-512
        public static final String PS512 = "PS512";

        // EC 相关

        // SHA256withECDSA
        // ECDSA using P-256 and SHA-256
        public static final String ES256 = "ES256";

        public static final String ES256K = "ES256K";

        // SHA384withECDSA
        // ECDSA using P-384 and SHA-384
        public static final String ES384 = "ES384";

        // SHA512withECDSA
        // ECDSA using P-521 and SHA-512
        public static final String ES512 = "ES512";

        // EdDSA
        public static final String EdDSA = "EdDSA";
    }

    public static JWTService getJWTService() {
        return Pipeline.<JWTService>of(new CommonServiceProvider<JWTService>().get(JWTService.class))
                .findFirst();
    }

    public static JWSToken newJWTPlainToken(Map<String, Object> header, Map<String, Object> payload) {
        return getJWTService()
                .newJWSTokenBuilder()
                .withHeader(header)
                .withAlgorithm(JWT_ALGORITHM_PLAIN)
                .withType(JWT_TYPE_DEFAULT)
                .withPayload(payload)
                .plain();
    }

    /**
     * @param signAlgorithm @see JWTs.JWSAlgorithms
     * @param payload       the payload
     * @param secretKey     sign with a secret key
     * @return a jws token
     */
    public static JWSToken newJWTSignedToken(String signAlgorithm, Map<String, Object> header, Map<String, Object> payload, SecretKey secretKey) {
        if (Strings.isBlank(signAlgorithm) || !Signs.supportedJWTHMacAlgorithms().contains(signAlgorithm)) {
            throw new JWTException("invalid jwt sign ( hmac sha ) algorithm: " + signAlgorithm);
        }
        return getJWTService()
                .newJWSTokenBuilder()
                .withHeader(header)
                .withAlgorithm(signAlgorithm)
                .withType(JWT_TYPE_DEFAULT)
                .withPayload(payload)
                .sign(secretKey);
    }

    public static JWSToken newJWTSignedToken(String signAlgorithm, Map<String, Object> header, Map<String, Object> payload, PrivateKey privateKey) {
        if (Strings.isBlank(signAlgorithm) || !Signs.supportedJWTPKIAlgorithms().contains(signAlgorithm)) {
            throw new JWTException("invalid jwt sign ( private key ) algorithm: " + signAlgorithm);
        }
        return getJWTService()
                .newJWSTokenBuilder()
                .withHeader(header)
                .withAlgorithm(signAlgorithm)
                .withType(JWT_TYPE_DEFAULT)
                .withPayload(payload)
                .sign(privateKey);
    }

    public static <T extends JWT> T parse(String token) {
        return (T)getJWTService().newParser().parse(token);
    }

    public static boolean isPlainToken(String algorithm) {
        return getAlgorithmType(algorithm) == AlgorithmType.NONE;
    }

    public static AlgorithmType getAlgorithmType(String algorithm) {
        return getJWTService().getAlgorithmType(algorithm);
    }

}
