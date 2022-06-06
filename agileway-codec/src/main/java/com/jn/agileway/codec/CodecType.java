package com.jn.agileway.codec;

import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

public enum CodecType implements CommonEnum {
    ACTIVEJ(0, "activej"),
    BSON(5, "bson"),
    CBOR(10, "cbor"),
    FSE(15, "fse"),
    FST(20, "fst"),
    HESSIAN(25, "hessian"),
    JACKSON(30, "jackson"),
    JDK(35, "jdk"),
    JSON(40, "json"), // easyjson
    KRYO(45, "kryo"),
    MSGPACK(50, "msgpack"),
    PROTOSTUFF(55, "protostuff"),
    JAVABEANS_XML(60, "javabeans_xml"),
    XSON(65, "xson");
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
