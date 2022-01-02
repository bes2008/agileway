package com.jn.agileway.ssh.client.transport.hostkey.verifier;

public class PromiscuousHostKeyVerifier implements HostKeyVerifier {
    private boolean expect = true;

    public PromiscuousHostKeyVerifier() {
    }

    public PromiscuousHostKeyVerifier(boolean expect) {
        this.expect = expect;
    }

    @Override
    public boolean verify(String hostname, int port, String serverHostKeyAlgorithm, Object publicKey) {
        return expect;
    }
}
