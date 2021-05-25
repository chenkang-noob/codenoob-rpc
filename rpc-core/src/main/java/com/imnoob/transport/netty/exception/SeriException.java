package com.imnoob.transport.netty.exception;

import com.imnoob.transport.netty.enums.CustomizeException;
import lombok.Data;

@Data
public class SeriException extends RuntimeException {

    private Integer code;
    private String msg;

    public SeriException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public SeriException(CustomizeException e) {
        super(e.getMsg());
        this.code = e.getCode();
        this.msg = e.getMsg();
    }
}
