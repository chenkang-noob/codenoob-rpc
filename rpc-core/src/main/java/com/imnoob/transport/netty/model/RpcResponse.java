package com.imnoob.transport.netty.model;


import com.imnoob.transport.netty.enums.ResponseCode;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class RpcResponse<T> implements Serializable {

    /**
     * 响应对应的请求号
     */
    private String requestId;
    /**
     * 响应状态码
     */
    private Integer statusCode;
    /**
     * 响应状态补充信息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;


    public static RpcResponse OK(String requestId) {
        RpcResponse<Object> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setMessage(ResponseCode.OK.getMsg());
        response.setStatusCode(ResponseCode.OK.getCode());
        return response;
    }

    public static RpcResponse OK(String requestId,Object obj) {
        RpcResponse ok = OK(requestId);
        ok.setData(obj);
        return ok;
    }

    public static RpcResponse ERROR(String requestId,Integer code,String msg) {
        RpcResponse<Object> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setMessage(msg);
        response.setStatusCode(code);
        return response;
    }

    public static RpcResponse ERROR(String requestId,ResponseCode en) {
        RpcResponse<Object> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setMessage(en.getMsg());
        response.setStatusCode(en.getCode());
        return response;
    }

}
