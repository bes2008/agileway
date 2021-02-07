package com.jn.agileway.ssh.client.transport.kex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;

public class KnownHostsVerifier implements HostKeyVerifier{
    private static final Logger logger = LoggerFactory.getLogger(KnownHostsVerifier.class);
    @Override
    public boolean verifyServerHostKey(String hostname, int port, PublicKey key){
        logger.warn(logger.getName());
        return false;
    }
}
