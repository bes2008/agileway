package com.jn.agileway.codec.serialization.fst;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.codec.CodecException;
import org.nustaq.serialization.FSTConfiguration;

public class FstCodec<T> extends AbstractCodec<T> {
    @Nullable
    private FSTConfiguration fst = Fsts.fstFactory.get();


    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        try {
            return Fsts.serialize(fst, t);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
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
