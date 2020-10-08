package com.jn.agileway.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jn.langx.factory.Factory;
import com.jn.langx.factory.ThreadLocalFactory;
import com.jn.langx.util.io.IOs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Kryos {
    private Kryos() {
    }

    public static final ThreadLocalFactory<?, Kryo> kryoFactory = new ThreadLocalFactory<Object, Kryo>(new Factory<Object, Kryo>() {
        @Override
        public Kryo get(Object o) {
            Kryo kryo = new Kryo();
            kryo.setReferences(true);
            return kryo;
        }
    });


    public static <T> byte[] serialize(T o) throws IOException {
        return serialize(kryoFactory, o);
    }

    public static <T> byte[] serialize(Factory<?, Kryo> kryoFactory, T o) {
        if (o == null) {
            return null;
        }

        Output output = null;
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            output = new Output(bao);
            kryoFactory.get(null).writeClassAndObject(output, o);
            return bao.toByteArray();
        } finally {
            IOs.close(output);
        }
    }

    public static <T> T deserialize(byte[] bytes) throws IOException {
        return deserialize(kryoFactory, bytes);
    }


    public static <T> T deserialize(Factory<?, Kryo> kryoFactory, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Input input = null;
        try {

            ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
            input = new Input(bai);
            return (T) kryoFactory.get(null).readClassAndObject(input);
        } finally {
            IOs.close(input);
        }
    }

}
