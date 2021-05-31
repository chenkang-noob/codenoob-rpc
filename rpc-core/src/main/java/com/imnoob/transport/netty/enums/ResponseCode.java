package com.imnoob.transport.netty.enums;

public enum ResponseCode {
    OK(200,"请求成功"),
    SYSTEM_ERROR(500,"系统错误"),
    METHOM_CALL_ERROR(501,"方法调用失败"),
    SERVICE_NOT_FOUND(502,"服务未发现");

    private Integer code;
    private String msg;

    ResponseCode(Integer code, String msg) {
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
