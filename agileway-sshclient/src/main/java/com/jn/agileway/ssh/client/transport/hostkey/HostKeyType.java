package com.jn.agileway.ssh.client.transport.hostkey;

import com.jn.agileway.ssh.client.utils.Buffer;
import com.jn.langx.security.crypto.IllegalKeyException;
import com.jn.langx.security.crypto.key.PKIs;
import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public enum HostKeyType implements CommonEnum {
    SSH_DSS(1, "ssh-dss", "ssh-dss") {
        @Override
        public PublicKey read(Buffer<?> buf) throws IllegalKeyException {
            BigInteger p, q, g, y;
            try {
                p = buf.readMPInt();
                q = buf.readMPInt();
                g = buf.readMPInt();
                y = buf.readMPInt();
            } catch (Buffer.BufferException be) {
                throw new IllegalKeyException(be);
            }
            try {
                return PKIs.getKeyFactory("DSA", null).generatePublic(new DSAPublicKeySpec(y, p, q, g));
            } catch (InvalidKeySpecException ex) {
                throw new IllegalKeyException();
            }
        }

        @Override
        public void write(PublicKey pk, Buffer<?> buf) {
            final DSAPublicKey dsaKey = (DSAPublicKey) pk;
            buf.putString(getName())
                    .putMPInt(dsaKey.getParams().getP()) // p
                    .putMPInt(dsaKey.getParams().getQ()) // q
                    .putMPInt(dsaKey.getParams().getG()) // g
                    .putMPInt(dsaKey.getY()); // y
        }
    },
    SSH_RSA(2, "ssh-rsa", "ssh-rsa") {
        @Override
        public PublicKey read(Buffer<?> buf) throws IllegalKeyException {
            final BigInteger e, n;
            try {
                e = buf.readMPInt();
                n = buf.readMPInt();

                return PKIs.getKeyFactory("RSA", null).generatePublic(new RSAPublicKeySpec(n, e));
            } catch (Buffer.BufferException be) {
                throw new IllegalKeyException(be);
            } catch (InvalidKeySpecException ex) {
                throw new IllegalKeyException(ex);
            }
        }

        @Override
        public void write(PublicKey pk, Buffer<?> buf) {
            final RSAPublicKey rsaKey = (RSAPublicKey) pk;
            buf.putString(getName())
                    .putMPInt(rsaKey.getPublicExponent()) // e
                    .putMPInt(rsaKey.getModulus()); // n
        }
    }

    /*
    ,
    ECDSA_SHA2_NISTP256(3, "ecdsa-sha2-nistp256", "ecdsa-sha2-nistp256") {
        @Override
        public PublicKey read(Buffer<?> buf) throws IllegalKeyException {
            return null;
        }

        @Override
        public void write(PublicKey pk, Buffer<?> buf) {

        }
    },
    ECDSA_SHA2_NISTP384(4, "ecdsa-sha2-nistp384", "ecdsa-sha2-nistp384") {
        @Override
        public PublicKey read(Buffer<?> buf) throws IllegalKeyException {
            return null;
        }

        @Override
        public void write(PublicKey pk, Buffer<?> buf) {

        }
    },
    ECDSA_SHA2_NISTP521(5, "ecdsa-sha2-nistp521", "ecdsa-sha2-nistp521") {
        @Override
        public PublicKey read(Buffer<?> buf) throws IllegalKeyException {
            return null;
        }

        @Override
        public void write(PublicKey pk, Buffer<?> buf) {

        }
    }
    */;
    private EnumDelegate delegate;

    HostKeyType(int code, String name, String displayText) {
        this.delegate = new EnumDelegate(code, name, displayText);
    }

    @Override
    public int getCode() {
        return this.delegate.getCode();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    public abstract PublicKey read(Buffer<?> buf) throws IllegalKeyException;

    public abstract void write(PublicKey pk, Buffer<?> buf);

    @Override
    public String getDisplayText() {
        return this.delegate.getDisplayText();
    }

}
