package com.jn.agileway.jwt.ec;

import com.jn.agileway.jwt.JWTs;
import java.security.spec.ECParameterSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ECurves {

    /**
     * P-256 curve (secp256r1, also called prime256v1, OID =
     * 1.2.840.10045.3.1.7).
     */
    public static final ECurve P_256 = new ECurve("P-256", "secp256r1", "1.2.840.10045.3.1.7");
    /**
     * secp256k1 curve (secp256k1, OID = 1.3.132.0.10).
     * P_256K
     */
    public static final ECurve SECP256K1 = new ECurve("secp256k1", "secp256k1", "1.3.132.0.10");

    /**
     * P-256K curve.
     */
    public static final ECurve P_256K = new ECurve("P-256K", "secp256k1", "1.3.132.0.10");

    /**
     * P-384 curve (secp384r1, OID = 1.3.132.0.34).
     */
    public static final ECurve P_384 = new ECurve("P-384", "secp384r1", "1.3.132.0.34");

    /**
     * P-521 curve (secp521r1).
     */
    public static final ECurve P_521 = new ECurve("P-521", "secp521r1", "1.3.132.0.35");

    /**
     * Ed25519 signature algorithm key pairs.
     */
    public static final ECurve Ed25519 = new ECurve("Ed25519", "Ed25519", null);


    /**
     * Ed448 signature algorithm key pairs.
     */
    public static final ECurve Ed448 = new ECurve("Ed448", "Ed448", null);

    /**
     * X25519 function key pairs.
     */
    public static final ECurve X25519 = new ECurve("X25519", "X25519", null);

    /**
     * X448 function key pairs.
     */
    public static final ECurve X448 = new ECurve("X448", "X448", null);

    /**
     * Parses a cryptographic curve from the specified string.
     *
     * @param s The string to parse. Must not be {@code null} or empty.
     *
     * @return The cryptographic curve.
     */
    public static ECurve parse(final String s) {

        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("The cryptographic curve string must not be null or empty");
        }

        if (s.equals(P_256.getName())) {
            return P_256;
        } else if (s.equals(P_256K.getName())) {
            return P_256K;
        } else if (s.equals(SECP256K1.getName())) {
            return SECP256K1;
        } else if (s.equals(P_384.getName())) {
            return P_384;
        } else if (s.equals(P_521.getName())) {
            return P_521;
        } else if (s.equals(Ed25519.getName())) {
            return Ed25519;
        } else if (s.equals(Ed448.getName())) {
            return Ed448;
        } else if (s.equals(X25519.getName())) {
            return X25519;
        } else if (s.equals(X448.getName())) {
            return X448;
        } else {
            return new ECurve(s);
        }
    }



    /**
     * Gets the cryptographic curve(s) for the specified JWS algorithm.
     *
     * @param alg The JWS algorithm. May be {@code null}.
     *
     * @return The curve(s), {@code null} if the JWS algorithm is not curve
     *         based, or the JWS algorithm is not supported.
     */
    public static Set<ECurve> forJWSAlgorithm(final String alg) {

        if (JWTs.JWSAlgorithms.ES256.equals(alg)) {
            return Collections.singleton(P_256);
        } else if (JWTs.JWSAlgorithms.ES256K.equals(alg)) {
            return Collections.singleton(SECP256K1);
        } else if (JWTs.JWSAlgorithms.ES384.equals(alg)) {
            return Collections.singleton(P_384);
        } else if (JWTs.JWSAlgorithms.ES512.equals(alg)) {
            return Collections.singleton(P_521);
        } else if (JWTs.JWSAlgorithms.EdDSA.equals(alg)) {
            return Collections.unmodifiableSet(
                    new HashSet<>(Arrays.asList(
                            Ed25519,
                            Ed448
                    ))
            );
        } else {
            return null;
        }
    }


    /**
     * Gets the cryptographic curve for the specified standard
     * name.
     *
     * @param stdName The standard curve name. May be {@code null}.
     *
     * @return The curve, {@code null} if it cannot be determined.
     */
    public static ECurve forStdName(final String stdName) {
        if( "secp256r1".equals(stdName) || "prime256v1".equals(stdName)) {
            return P_256;
        } else if("secp256k1".equals(stdName)) {
            return SECP256K1;
        } else if("secp384r1".equals(stdName)) {
            return P_384;
        } else if("secp521r1".equals(stdName)) {
            return P_521;
        } else if (Ed25519.getStdName().equals(stdName)) {
            return Ed25519;
        } else if (Ed448.getStdName().equals(stdName)) {
            return Ed448;
        } else if (X25519.getStdName().equals(stdName)) {
            return X25519;
        } else if (X448.getStdName().equals(stdName)) {
            return X448;
        } else {
            return null;
        }
    }


    /**
     * Gets the cryptographic curve for the specified object identifier
     * (OID).
     *
     * @param oid The object OID. May be {@code null}.
     *
     * @return The curve, {@code null} if it cannot be determined.
     */
    public static ECurve forOID(final String oid) {

        if (P_256.getOID().equals(oid)) {
            return P_256;
        } else if (SECP256K1.getOID().equals(oid)) {
            return SECP256K1;
        } else if (P_384.getOID().equals(oid)) {
            return P_384;
        } else if (P_521.getOID().equals(oid)) {
            return P_521;
        } else {
            return null;
        }
    }



    /**
     * Gets the cryptographic curve for the specified parameter
     * specification.
     *
     * @param spec The EC parameter spec. May be {@code null}.
     *
     * @return The curve, {@code null} if it cannot be determined.
     */
    public static ECurve forECParameterSpec(final ECParameterSpec spec) {
        return ECurveParameterTable.get(spec);
    }
}
