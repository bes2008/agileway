package com.jn.agileway.ssh.client.transport.hostkey.keytype;

import com.jn.agileway.ssh.client.utils.Buffer;

public class KeyBufferHostKeyTypeExtractor implements HostKeyTypeExtractor<Buffer> {
    public static final KeyBufferHostKeyTypeExtractor INSTANCE=new KeyBufferHostKeyTypeExtractor();
    private KeyBufferHostKeyTypeExtractor(){}

    @Override
    public String get(Buffer buffer) {
        final String keyType = buffer.readString();
        return keyType;
    }
}
