package com.imnoob.transport.netty.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.imnoob.transport.netty.enums.CustomizeException;
import com.imnoob.transport.netty.exception.SeriException;
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
            throw new SeriException(CustomizeException.NOT_FOUND_SERIALIZER_TYPE); //TODO 抛出异常
    }



     byte[] serializer(Object obj);

     Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();
}
