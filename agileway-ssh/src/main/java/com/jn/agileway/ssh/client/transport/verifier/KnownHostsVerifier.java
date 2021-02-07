package com.jn.agileway.ssh.client.transport.verifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;

public class KnownHostsVerifier implements HostKeyVerifier{
    private static final Logger logger = LoggerFactory.getLogger(KnownHostsVerifier.class);
    @Override
    public boolean verify(String hostname, int port, byte[] key){
        logger.warn(logger.getName());
        return false;
    }
}
