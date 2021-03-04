package com.jn.agileway.codec.tests;

import com.jn.agileway.codec.Codec;
import com.jn.agileway.codec.CodecException;
import com.jn.agileway.codec.serialization.cbor.CborJacksonCodec;
import com.jn.agileway.codec.serialization.fse.FseCodec;
import com.jn.agileway.codec.serialization.fst.FstCodec;
import com.jn.agileway.codec.serialization.hessian.HessianCodec;
import com.jn.agileway.codec.serialization.jdk.JdkCodec;
import com.jn.agileway.codec.serialization.json.EasyjsonCodec;
import com.jn.agileway.codec.serialization.json.JacksonCodec;
import com.jn.agileway.codec.serialization.kryo.KryoCodec;
import com.jn.agileway.codec.serialization.msgpack.MsgPackCodec;
import com.jn.agileway.codec.serialization.protostuff.ProtostuffCodec;
import com.jn.agileway.codec.serialization.xson.XsonCodec;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class CodecTests {
    private static Pojo pojo = createPojo(true);
    private static Pojo pojo_non_cycle = createPojo(false);

    private static Pojo createPojo(boolean cycleRef) {
        Pojo pojo = new Pojo();
        pojo.setAttribute("zhangsan", "zhangsan");
        if (cycleRef) {
            pojo.setAttribute("pojo", pojo);
        }
        pojo.setHost("localhost");
        pojo.setId("234234=-23423wrfq23342341212312");
        pojo.setExpired(false);
        pojo.setStartTimestamp(new Date());

        return pojo;
    }

    private void testInternal(Pojo pojo, Codec<Pojo> codec) {
        byte[] bytes = codec.encode(pojo);
        Pojo pojo2 = codec.decode(bytes);
        Assert.assertEquals(pojo, pojo2);

        Pojo pojo3 = codec.decode(bytes, Pojo.class);
        Assert.assertEquals(pojo, pojo3);
    }


    @Test
    public void testCbor() throws CodecException {
        Codec<Pojo> codec = new CborJacksonCodec<Pojo>();
        testInternal(pojo_non_cycle, codec);
        // 基于Jackson框架实现，由于Jackson框架不支持循环依赖，因而导致 Cbor 目前的实现，不支持循环依赖。
        // testInternal(pojo, codec);
    }


    @Test
    public void testFse() throws CodecException {
        Codec<Pojo> codec = new FseCodec<Pojo>();
        testInternal(pojo_non_cycle, codec);
        testInternal(pojo, codec);
    }

    @Test
    public void testFst() throws CodecException {
        Codec<Pojo> codec = new FstCodec<>();
        // 目前Fst 有Bug，官方也不修复了，没法使用
        // testInternal(pojo_non_cycle, codec);
        // testInternal(pojo, codec);
    }


    @Test
    public void testHessian() throws CodecException {
        Codec<Pojo> codec = new HessianCodec();
        testInternal(pojo_non_cycle, codec);
        testInternal(pojo, codec);
    }

    @Test
    public void testJdk() {
        Codec<Pojo> codec = new JdkCodec<>();
        testInternal(pojo_non_cycle, codec);
        testInternal(pojo, codec);
    }

    @Test
    public void testEasyJson() {
        EasyjsonCodec<Pojo> codec = new EasyjsonCodec<>();
        codec.setSerializeType(true);
        testInternal(pojo_non_cycle, codec);
        // 底层若是采用jackson，则不支持循环依赖
        // testInternal(pojo, codec);
    }

    @Test
    public void testJackson() {
        Codec<Pojo> codec = new JacksonCodec<>();
        testInternal(pojo_non_cycle, codec);
        // Jackson框架不支持循环依赖
        // testInternal(pojo, codec);
    }


    @Test
    public void testKryo() throws CodecException {
        Codec<Pojo> codec = new KryoCodec<>();
        testInternal(pojo_non_cycle, codec);
        testInternal(pojo, codec);
    }

    @Test
    public void testMsgPack() {
        Codec<Pojo> codec = new MsgPackCodec<>();
        // 基于Jackson框架实现，由于Jackson框架不支持循环依赖，因而导致 MsgPack 目前的实现，不支持循环依赖。
        testInternal(pojo_non_cycle, codec);
        // testInternal(pojo, codec);
    }


    @Test
    public void testProtostuff() throws CodecException {
        Codec<Pojo> codec = new ProtostuffCodec<Pojo>();
        testInternal(pojo_non_cycle, codec);
        testInternal(pojo, codec);
    }


    @Test
    public void testXSON() throws CodecException {
        Codec<Pojo> codec = new XsonCodec<Pojo>();
        testInternal(pojo_non_cycle, codec);
        testInternal(pojo, codec);
    }


}
