package com.jn.agileway.codec.fst;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.CodecException;
import com.jn.langx.annotation.Nullable;
import org.nustaq.serialization.FSTConfiguration;

public class FstCodec<T> extends AbstractCodec<T> {
    @Nullable
    private FSTConfiguration fst = Fsts.fstFactory.get();

    @Override
    public byte[] encode(T obj) throws CodecException {
        try {
            return Fsts.serialize(fst, obj);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes) throws CodecException {
        try {
            return Fsts.deserialize(fst, bytes, getTargetType());
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes, Class<T> targetType) throws CodecException {
        try {
            return Fsts.deserialize(fst, bytes, targetType);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    public FSTConfiguration getFst() {
        return fst;
    }

    public void setFst(FSTConfiguration fst) {
        if (fst != null) {
            this.fst = fst;
        }
    }
}
