package com.jn.agileway.codec.tests;

import com.jn.agileway.codec.Codec;
import com.jn.agileway.codec.CodecException;
import com.jn.agileway.codec.hessian.HessianCodec;
import com.jn.agileway.codec.kryo.KryoCodec;
import com.jn.agileway.codec.protostuff.ProtostuffCodec;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class CodecTests {
    private static Pojo pojo = createPojo();

    private static Pojo createPojo() {
        Pojo pojo = new Pojo();
        pojo.setAttribute("zhangsan", "zhangsan");
        pojo.setAttribute("pojo", pojo);
        pojo.setHost("localhost");
        pojo.setId("234234=-23423wrfq23342341212312");
        pojo.setExpired(false);
        pojo.setStartTimestamp(new Date());

        return pojo;
    }

    @Test
    public void testHessian() throws CodecException {
        Codec<Pojo> codec = new HessianCodec();
        byte[] bytes = codec.encode(pojo);
        Pojo pojo2 = codec.decode(bytes);
        Assert.assertEquals(pojo, pojo2);

        Pojo pojo3 = codec.decode(bytes, Pojo.class);
        Assert.assertEquals(pojo, pojo3);
    }

    @Test
    public void testKryo() throws CodecException {
        Codec<Pojo> codec = new KryoCodec<>();
        byte[] bytes = codec.encode(pojo);
        Pojo pojo2 = codec.decode(bytes);
        Assert.assertEquals(pojo, pojo2);

        Pojo pojo3 = codec.decode(bytes, Pojo.class);
        Assert.assertEquals(pojo, pojo3);
    }


    @Test
    public void testProtostuff() throws CodecException {
        Codec<Pojo> codec = new ProtostuffCodec<Pojo>();
        byte[] bytes = codec.encode(pojo);

        Pojo pojo3 = codec.decode(bytes, Pojo.class);
        Assert.assertEquals(pojo, pojo3);
    }

}
