package com.jn.agileway.codec.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.CodecException;
import com.jn.langx.util.Emptys;

public class KryoCodec<T> extends AbstractCodec<T> {
    private Kryo kryo;

    public Kryo getKryo() {
        return kryo == null ? Kryos.kryoFactory.get() : kryo;
    }

    public void setKryo(Kryo kryo) {
        this.kryo = kryo;
    }

    @Override
    public byte[] encode(T t) throws CodecException {
        try {
            return Kryos.serialize(getKryo(), t);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes) throws CodecException {
        try {
            return Kryos.deserialize(getKryo(), bytes, getTargetType());
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes, Class<T> targetType) throws CodecException {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        return Kryos.deserialize(getKryo(), bytes, targetType);
    }

}
