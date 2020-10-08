package com.jn.agileway.redis.core.serialization.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.jn.langx.util.io.IOs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Hessians {
    private Hessians() {
    }

    private static ThreadLocal<Hessian2Output> outputThreadLocal = new ThreadLocal<Hessian2Output>() {
        @Override
        protected Hessian2Output initialValue() {
            return new Hessian2Output();
        }
    };

    private static ThreadLocal<Hessian2Input> inputThreadLocal = new ThreadLocal<Hessian2Input>() {
        @Override
        protected Hessian2Input initialValue() {
            return new Hessian2Input();
        }
    };

    public static <T> byte[] serialize(T o) throws IOException {
        if (o == null) {
            return null;
        }

        Hessian2Output output = null;
        try {
            output = outputThreadLocal.get();
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            output.init(bao);
            output.writeObject(o);
            return bao.toByteArray();
        } finally {
            IOs.close(output);
        }
    }

    public static <T> T deserialize(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Hessian2Input input = null;
        try {
            input = inputThreadLocal.get();
            ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
            input.init(bai);
            return (T) input.readObject();
        } finally {
            IOs.close(input);
        }
    }

}
