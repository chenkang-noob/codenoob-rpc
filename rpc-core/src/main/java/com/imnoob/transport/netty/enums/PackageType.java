package com.imnoob.transport.netty.enums;

public enum PackageType {
    RPC_REQUEST(0),
    RPC_RESPONSE(1);

    private final int code;

    public int getCode() {
        return code;
    }

    PackageType(int code) {
        this.code = code;
    }
}
