package com.jn.agileway.ssh.client.transport.hostkey.verifier;

import com.jn.langx.util.collection.Collects;

import java.util.List;

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

    @Override
    public List<String> findExistingAlgorithms(String hostname, int port) {
        return Collects.immutableList();
    }
}
