package com.imnoob.transport.netty.enums;

import lombok.Data;

public enum CustomizeException {
    SERIALIZER_ERROR(1000,"序列化异常"),
    NOT_FOUND_SERIALIZER_TYPE(1001,"为找到对应的序列化方式");

    private Integer code;
    private String msg;

    CustomizeException(Integer code,String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
