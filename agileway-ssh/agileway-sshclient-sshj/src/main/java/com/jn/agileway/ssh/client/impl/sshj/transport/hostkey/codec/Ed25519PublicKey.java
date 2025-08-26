package com.jn.agileway.ssh.client.impl.sshj.transport.hostkey.codec;

import com.jn.agileway.ssh.client.SshException;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

import java.util.Arrays;

/**
 * Our own extension of the EdDSAPublicKey that comes from ECC-25519, as that class does not implement equality.
 * The code uses the equality of the keys as an indicator whether they're the same during host key verification.
 */
class Ed25519PublicKey extends EdDSAPublicKey {

    public Ed25519PublicKey(EdDSAPublicKeySpec spec) {
        super(spec);

        EdDSANamedCurveSpec ed25519 = EdDSANamedCurveTable.getByName("Ed25519");
        if (!spec.getParams().getCurve().equals(ed25519.getCurve())) {
            throw new SshException("Cannot create Ed25519 Public Key from wrong spec");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Ed25519PublicKey)) {
            return false;
        }

        Ed25519PublicKey otherKey = (Ed25519PublicKey) other;
        return Arrays.equals(getAbyte(), otherKey.getAbyte());
    }

    @Override
    public int hashCode() {
        return getA().hashCode();
    }
}
