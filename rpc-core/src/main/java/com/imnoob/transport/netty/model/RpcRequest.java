package com.imnoob.transport.netty.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcRequest implements Serializable {

    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;

}
