package com.imnoob.transport.netty.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.imnoob.transport.netty.model.RpcRequest;

import java.io.IOException;

public interface CommonSerializer {

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;

    static CommonSerializer getSerializer(int code){
        if (code == 0) return null;
        else if (code == 1) return new JsonSerializer();
        else if (code == 2) return null;
        else if (code == 3) return null;
        else
            return null; //TODO 抛出异常
    }



    public byte[] serializer(Object obj);

    public Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();
}
