package com.imnoob.transport.netty.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@Data

public class RpcRequest implements Serializable {

    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcRequest request = (RpcRequest) o;
        return requestId.equals(request.requestId) &&
                interfaceName.equals(request.interfaceName) &&
                methodName.equals(request.methodName) &&
                Arrays.equals(parameters, request.parameters) &&
                Arrays.equals(paramTypes, request.paramTypes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(interfaceName, methodName);
        result = 31 * result + Arrays.hashCode(parameters);
        result = 31 * result + Arrays.hashCode(paramTypes);
        return result;
    }



}
