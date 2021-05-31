package com.imnoob.transport.netty.enums;

import lombok.Data;

public enum CustomizeException {
    SERIALIZER_ERROR(1000,"序列化异常"),
    NOT_FOUND_SERIALIZER_TYPE(1001,"为找到对应的序列化方式"),
    NACOS_TIMEOUT(1002,"Nacos超时异常"),
    NETTY_CLIENT_TIMEOUT(1003,"客户端连接超时"),
    SEND_MSG_ERROR(1004,"消息发送失败"),
    RUN_ANNOTATION_ERROR(1005,"启动类注解异常"),
    ANNOTATION_SCAN_ERROR(1006,"注解扫描异常"),
    LIMITE_RATE_ERROR(1007,"限流，请稍后访问");


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
