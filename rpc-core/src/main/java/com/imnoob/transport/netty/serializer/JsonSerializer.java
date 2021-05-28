package com.imnoob.transport.netty.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imnoob.transport.netty.enums.CustomizeException;
import com.imnoob.transport.netty.enums.SerializeType;
import com.imnoob.transport.netty.exception.CommonException;
import com.imnoob.transport.netty.model.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonSerializer implements CommonSerializer {

    //TODO 序列化和反序列

    ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    public JsonSerializer() {

    }

    public byte[] serializer(Object obj){
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("序列化时有错误发生:", e);
            e.printStackTrace();
            throw new CommonException(CustomizeException.SERIALIZER_ERROR);
        }

    }

    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RpcRequest) {
                RpcRequest request = (RpcRequest) obj;
                logger.info("收到请求："+request.getRequestId());
            }
            return obj;
        } catch (IOException e) {
            logger.error("序列化时有错误发生:", e);
            throw new CommonException(CustomizeException.SERIALIZER_ERROR);
        }
    }

    @Override
    public int getCode() {
        return CommonSerializer.JSON_SERIALIZER;
    }
}
