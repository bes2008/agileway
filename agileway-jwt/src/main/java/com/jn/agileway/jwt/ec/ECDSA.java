package com.jn.agileway.jwt.ec;

import com.jn.agileway.jwt.JWTException;
import com.jn.agileway.jwt.JWTs;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.collection.Sets;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.ECDSAProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECParameterTable;
import com.nimbusds.jose.util.ByteUtils;

import java.math.BigInteger;
import java.security.spec.ECParameterSpec;
import java.util.Map;
import java.util.Set;

public class ECDSA {
    private static Map<String, Integer> ECDSA_Algorithms_Signature_Bytes_Length_Map;

    static {
        Map<String, Integer> map = Maps.newHashMap();
        map.put(JWTs.JWSAlgorithms.ES256, 64);
        map.put(JWTs.JWSAlgorithms.ES256K, 64);
        map.put(JWTs.JWSAlgorithms.ES384, 96);
        map.put(JWTs.JWSAlgorithms.ES512, 132);
        ECDSA_Algorithms_Signature_Bytes_Length_Map = map;
    }

    public static boolean isECJWSAlgorithm(String jwsAlgorithm) {
        return ECDSA_Algorithms_Signature_Bytes_Length_Map.containsKey(jwsAlgorithm);
    }


    /**
     * Returns the expected signature byte array length (R + S parts) for
     * the specified ECDSA algorithm.
     *
     * @param alg The ECDSA algorithm. Must be supported and not
     *            {@code null}.
     * @return The expected byte array length for the signature.
     * @throws JWTException If the algorithm is not supported.
     */
    public static int getSignatureByteArrayLength(final String alg) throws JWTException {
        if (isECJWSAlgorithm(alg)) {
            return ECDSA_Algorithms_Signature_Bytes_Length_Map.get(alg);
        } else {
            throw new JWTException("unsupported ec dsa algorithm");
        }
    }


    /**
     * Transcodes the JCA ASN.1/DER-encoded signature into the concatenated
     * R + S format expected by ECDSA JWS.
     *
     * @param derSignature The ASN1./DER-encoded. Must not be {@code null}.
     * @param outputLength The expected length of the ECDSA JWS signature.
     * @return The ECDSA JWS encoded signature.
     * @throws JWTException If the ASN.1/DER signature format is invalid.
     */
    public static byte[] transcodeSignatureToConcat(final byte[] derSignature, final int outputLength) throws JWTException {

        if (derSignature.length < 8 || derSignature[0] != 48) {
            throw new JWTException("Invalid ECDSA signature format");
        }

        int offset;
        if (derSignature[1] > 0) {
            offset = 2;
        } else if (derSignature[1] == (byte) 0x81) {
            offset = 3;
        } else {
            throw new JWTException("Invalid ECDSA signature format");
        }

        byte rLength = derSignature[offset + 1];

        int i;
        for (i = rLength; (i > 0) && (derSignature[(offset + 2 + rLength) - i] == 0); i--) {
            // do nothing
        }

        byte sLength = derSignature[offset + 2 + rLength + 1];

        int j;
        for (j = sLength; (j > 0) && (derSignature[(offset + 2 + rLength + 2 + sLength) - j] == 0); j--) {
            // do nothing
        }

        int rawLen = Math.max(i, j);
        rawLen = Math.max(rawLen, outputLength / 2);

        if ((derSignature[offset - 1] & 0xff) != derSignature.length - offset
                || (derSignature[offset - 1] & 0xff) != 2 + rLength + 2 + sLength
                || derSignature[offset] != 2
                || derSignature[offset + 2 + rLength] != 2) {
            throw new JWTException("Invalid ECDSA signature format");
        }

        final byte[] concatSignature = new byte[2 * rawLen];

        System.arraycopy(derSignature, (offset + 2 + rLength) - i, concatSignature, rawLen - i, i);
        System.arraycopy(derSignature, (offset + 2 + rLength + 2 + sLength) - j, concatSignature, 2 * rawLen - j, j);

        return concatSignature;
    }


    /**
     * Transcodes the ECDSA JWS signature into ASN.1/DER format for use by
     * the JCA verifier.
     *
     * @param jwsSignature The JWS signature, consisting of the
     *                     concatenated R and S values. Must not be
     *                     {@code null}.
     * @return The ASN.1/DER encoded signature.
     * @throws JWTException If the ECDSA JWS signature format is invalid
     *                      or conversion failed unexpectedly.
     */
    public static byte[] transcodeSignatureToDER(final byte[] jwsSignature) throws JWTException {

        // Adapted from org.apache.xml.security.algorithms.implementations.SignatureECDSA
        try {
            int rawLen = jwsSignature.length / 2;

            int i;

            for (i = rawLen; (i > 0) && (jwsSignature[rawLen - i] == 0); i--) {
                // do nothing
            }

            int j = i;

            if (jwsSignature[rawLen - i] < 0) {
                j += 1;
            }

            int k;

            for (k = rawLen; (k > 0) && (jwsSignature[2 * rawLen - k] == 0); k--) {
                // do nothing
            }

            int l = k;

            if (jwsSignature[2 * rawLen - k] < 0) {
                l += 1;
            }

            int len = 2 + j + 2 + l;

            if (len > 255) {
                throw new JWTException("Invalid ECDSA signature format");
            }

            int offset;

            final byte[] derSignature;

            if (len < 128) {
                derSignature = new byte[2 + 2 + j + 2 + l];
                offset = 1;
            } else {
                derSignature = new byte[3 + 2 + j + 2 + l];
                derSignature[1] = (byte) 0x81;
                offset = 2;
            }

            derSignature[0] = 48;
            derSignature[offset++] = (byte) len;
            derSignature[offset++] = 2;
            derSignature[offset++] = (byte) j;

            System.arraycopy(jwsSignature, rawLen - i, derSignature, (offset + j) - i, i);

            offset += j;

            derSignature[offset++] = 2;
            derSignature[offset++] = (byte) l;

            System.arraycopy(jwsSignature, 2 * rawLen - k, derSignature, (offset + l) - k, k);

            return derSignature;

        } catch (Exception e) {
            // Watch for unchecked exceptions
            if (e instanceof JWTException) {
                throw e;
            }
            throw new JWTException(e.getMessage(), e);
        }
    }


    /**
     * Ensures the specified ECDSA signature is legal. Intended to prevent
     * attacks on JCA implementations vulnerable to CVE-2022-21449 and
     * similar bugs.
     *
     * @param jwsSignature The JWS signature. Must not be {@code null}.
     * @param jwsAlg       The ECDSA JWS algorithm. Must not be
     *                     {@code null}.
     * @throws JWTException If the signature is found to be illegal, or
     *                      the JWS algorithm or curve are not supported.
     */
    public static void ensureLegalSignature(final byte[] jwsSignature, final String jwsAlg) throws JWTException {

        if (ByteUtils.isZeroFilled(jwsSignature)) {
            // Quick check to make sure S and R are not both zero (CVE-2022-21449)
            throw new JWTException("Blank signature");
        }

        Set<ECurve> matchingCurves = ECurves.forJWSAlgorithm(jwsAlg);
        if (matchingCurves == null || matchingCurves.size() > 1) {
            throw new JWTException("Unsupported JWS algorithm: " + jwsAlg);
        }

        ECurve curve = matchingCurves.iterator().next();

        ECParameterSpec ecParameterSpec = ECurveParameterTable.get(curve);

        if (ecParameterSpec == null) {
            throw new JWTException("Unsupported curve: " + curve);
        }

        final int signatureLength = ECDSA.getSignatureByteArrayLength(jwsAlg);

        if (ECDSA.getSignatureByteArrayLength(jwsAlg) != jwsSignature.length) {
            // Quick format check, concatenation of R|S (may be padded
            // to match lengths) in ESxxx signatures has fixed length
            throw new JWTException("Illegal signature length");
        }

        // Split the signature bytes in the middle
        final int valueLength = signatureLength / 2;

        // Extract R
        final byte[] rBytes = ByteUtils.subArray(jwsSignature, 0, valueLength);
        final BigInteger rValue = new BigInteger(1, rBytes);

        // Extract S
        final byte[] sBytes = ByteUtils.subArray(jwsSignature, valueLength, valueLength);
        final BigInteger sValue = new BigInteger(1, sBytes);

        // Trivial zero check
        if (sValue.equals(BigInteger.ZERO) || rValue.equals(BigInteger.ZERO)) {
            throw new JWTException("S and R must not be 0");
        }

        final BigInteger N = ecParameterSpec.getOrder();

        // R and S must not be greater than the curve order N
        if (N.compareTo(rValue) < 1 || N.compareTo(sValue) < 1) {
            throw new JWTException("S and R must not exceed N");
        }

        // Extra paranoid check
        if (rValue.mod(N).equals(BigInteger.ZERO) || sValue.mod(N).equals(BigInteger.ZERO)) {
            throw new JWTException("R or S mod N != 0 check failed");
        }

        // Signature deemed legal, can proceed to DER transcoding and verification now
    }

}
