package com.jn.agileway.ssh.client.transport.hostkey.keytype;

import com.jn.agileway.ssh.client.utils.Buffer;

public class KeyBytesHostKeyTypeExtractor implements HostKeyTypeExtractor<byte[]> {
    public static final KeyBytesHostKeyTypeExtractor INSTANCE=new KeyBytesHostKeyTypeExtractor();
    private KeyBytesHostKeyTypeExtractor(){}
    @Override
    public String get(byte[] publicKey) {
        return KeyBufferHostKeyTypeExtractor.INSTANCE.get(new Buffer.PlainBuffer(publicKey));
    }
}
