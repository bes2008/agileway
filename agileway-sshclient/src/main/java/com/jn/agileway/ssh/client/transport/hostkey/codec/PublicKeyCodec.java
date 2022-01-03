package com.jn.agileway.ssh.client.transport.hostkey.codec;

import com.jn.langx.Named;
import com.jn.langx.codec.CodecException;
import com.jn.langx.codec.ICodec;

public interface PublicKeyCodec<T> extends ICodec<T> , Named {
    @Override
    T decode(byte[] bytes) throws CodecException;

    @Override
    byte[] encode(T t) throws CodecException;
}
