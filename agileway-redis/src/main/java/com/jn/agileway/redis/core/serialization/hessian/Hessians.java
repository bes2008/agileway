package com.jn.agileway.redis.core.serialization.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.jn.langx.util.io.IOs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Hessians {

    public static <T> byte[] serialize(T o) throws IOException {
        if (o == null) {
            return null;
        }

        Hessian2Output output = null;
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            output = new Hessian2Output(bao);
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
            ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
            input = new Hessian2Input(bai);
            return (T) input.readObject();
        } finally {
            IOs.close(input);
        }
    }

}
