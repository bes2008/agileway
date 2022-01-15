package com.jn.agileway.codec.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;

public class KryoCodec<T> extends AbstractCodec<T> {
    private Kryo kryo;

    public Kryo getKryo() {
        return kryo == null ? Kryos.kryoFactory.get() : kryo;
    }

    public void setKryo(Kryo kryo) {
        this.kryo = kryo;
    }

    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
           try {
            return Kryos.serialize(getKryo(), t);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        try {
            return Kryos.deserialize(getKryo(), bytes, targetType);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }


}
