package com.imnoob.transport.netty.enums;

public enum SerializeType {
    KryoSerializer(0),
    JsonSerializer(1),
    HessianSerializer(2),
    ProtobufSerializer(3);

    private final int code;

    public int getCode() {
        return code;
    }

    SerializeType(int code) {
        this.code = code;
    }
}
