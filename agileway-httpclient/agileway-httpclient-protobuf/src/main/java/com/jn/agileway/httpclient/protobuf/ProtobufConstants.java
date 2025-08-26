package com.jn.agileway.httpclient.protobuf;

public class ProtobufConstants {
    /**
     * The HTTP response header containing the protobuf schema.
     * <p>
     * 用于指定 protobuf schema （.proto）的 url
     */
    public static final String X_PROTOBUF_SCHEMA_HEADER = "X-Protobuf-Schema";

    /**
     * The HTTP response header containing the protobuf message.
     * <p>
     * 用于指定message 使用的是哪个类
     */
    public static final String X_PROTOBUF_MESSAGE_HEADER = "X-Protobuf-Message";

}
