package com.imnoob.transport.netty.enums;

public enum SerializeType {
    KRYO_SERIALIZER(0),
    JSON_SERIALIZER(1),
    PROTOBUF_SERIALIZER(2),
    DEFALUT_SERIALIZER(1);

    private final int code;

    public int getCode() {
        return code;
    }

    SerializeType(int code) {
        this.code = code;
    }
}
