package com.jn.agileway.ssh.client.impl.sshj.transport.hostkey.codec;

import com.jn.agileway.ssh.client.SshException;

import java.math.BigInteger;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Arrays;

class Secg {
    /**
     * SECG 2.3.4 Octet String to ECPoint
     */
    public static ECPoint getDecoded(byte[] M, EllipticCurve curve) {
        int elementSize = getElementSize(curve);
        if (M.length != 2 * elementSize + 1 || M[0] != 0x04) {
            throw new SshException("Invalid 'f' for Elliptic Curve " + curve.toString());
        }
        byte[] xBytes = new byte[elementSize];
        byte[] yBytes = new byte[elementSize];
        System.arraycopy(M, 1, xBytes, 0, elementSize);
        System.arraycopy(M, 1 + elementSize, yBytes, 0, elementSize);
        return new ECPoint(new BigInteger(1, xBytes), new BigInteger(1, yBytes));
    }

    /**
     * SECG 2.3.3 ECPoint to Octet String
     */
    public static byte[] getEncoded(ECPoint point, EllipticCurve curve) {
        int elementSize = getElementSize(curve);
        byte[] M = new byte[2 * elementSize + 1];
        M[0] = 0x04;

        byte[] xBytes = stripLeadingZeroes(point.getAffineX().toByteArray());
        byte[] yBytes = stripLeadingZeroes(point.getAffineY().toByteArray());
        System.arraycopy(xBytes, 0, M, 1 + elementSize - xBytes.length, xBytes.length);
        System.arraycopy(yBytes, 0, M, 1 + 2 * elementSize - yBytes.length, yBytes.length);
        return M;
    }

    private static byte[] stripLeadingZeroes(byte[] bytes) {
        int start = 0;
        while (bytes[start] == 0x0) {
            start++;
        }

        return Arrays.copyOfRange(bytes, start, bytes.length);
    }

    private static int getElementSize(EllipticCurve curve) {
        int fieldSize = curve.getField().getFieldSize();
        return (fieldSize + 7) / 8;
    }

}
