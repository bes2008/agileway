package com.jn.agileway.ssh.client.transport.hostkey;

import com.jn.agileway.ssh.client.transport.hostkey.codec.SshDssPublicKeyCodec;
import com.jn.agileway.ssh.client.transport.hostkey.codec.SshRsaPublicKeyCodec;
import com.jn.agileway.ssh.client.utils.Buffer;
import com.jn.langx.security.crypto.IllegalKeyException;
import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

import java.security.PublicKey;

public enum HostKeyType implements CommonEnum {
    SSH_DSS(1, "ssh-dss", "ssh-dss") {
        @Override
        public PublicKey read(Buffer<?> buf) throws IllegalKeyException {
            byte[] bytes = buf.remainingRawBytes();
            return new SshDssPublicKeyCodec().decode(bytes);
        }

        @Override
        public void write(PublicKey pk, Buffer<?> buf) {
            byte[] bytes = new SshDssPublicKeyCodec().encode(pk);
            buf.putRawBytes(bytes);
        }
    },
    SSH_RSA(2, "ssh-rsa", "ssh-rsa") {
        @Override
        public PublicKey read(Buffer<?> buf) throws IllegalKeyException {
            byte[] bytes = buf.remainingRawBytes();
            return new SshRsaPublicKeyCodec().decode(bytes);
        }

        @Override
        public void write(PublicKey pk, Buffer<?> buf) {
            byte[] bytes = new SshRsaPublicKeyCodec().encode(pk);
            buf.putRawBytes(bytes);
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
