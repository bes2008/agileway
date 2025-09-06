package com.jn.agileway.codec;

import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

public enum CodecType implements CommonEnum {
    ACTIVEJ(0, "activej"),
    AVRO(10, "avro"),
    BSON(20, "bson"),
    CBOR(30, "cbor"),
    FSE(40, "fse"),
    FST(50, "fst"),
    HESSIAN(60, "hessian"),
    JACKSON(70, "jackson"),
    JDK(80, "jdk"),
    JSON(90, "json"), // easyjson
    KRYO(100, "kryo"),
    MSGPACK(110, "msgpack"),
    PROTOSTUFF(120, "protostuff"),
    JAVABEANS_XML(130, "javabeans_xml"),
    XSON(140, "xson"),
    XSTREAM(150, "xstream"),
    SMIPLE_XML(160, "simple_xml"),
    JAVAX_JAXB(170, "javax_jaxb"),
    JAKARTA_JAXB(180, "jakart_jaxb"),
    XML(190, "xml");
    private EnumDelegate delegate;

    CodecType(int code, String name) {
        this.delegate = new EnumDelegate(code, name, name);
    }

    @Override
    public int getCode() {
        return this.delegate.getCode();
    }

    @Override
    public String getDisplayText() {
        return this.delegate.getDisplayText();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }
}
