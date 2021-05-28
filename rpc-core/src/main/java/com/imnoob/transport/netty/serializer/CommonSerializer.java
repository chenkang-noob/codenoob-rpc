package com.imnoob.transport.netty.serializer;

import com.imnoob.transport.netty.enums.CustomizeException;
import com.imnoob.transport.netty.exception.CommonException;

public interface CommonSerializer {

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer PROTOBUF_SERIALIZER = 2;


    static CommonSerializer getSerializer(int code){
        if (code == 0) return new KryoSerializer();
        else if (code == 1) return new JsonSerializer();
        else if (code == 2) return new KryoSerializer();
        else
            throw new CommonException(CustomizeException.NOT_FOUND_SERIALIZER_TYPE);
    }




     byte[] serializer(Object obj);

     Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();
}
