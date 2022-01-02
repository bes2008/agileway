package com.jn.agileway.ssh.client.transport.hostkey.verifier;

public class PromiscuousHostKeyVerifier implements HostKeyVerifier {
    @Override
    public boolean verify(String hostname, int port, String serverHostKeyAlgorithm, Object publicKey) {
        return true;
    }
}
