package com.imnoob.transport.netty.serializer;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.imnoob.transport.netty.enums.CustomizeException;
import com.imnoob.transport.netty.exception.CommonException;
import com.imnoob.transport.netty.model.RpcRequest;
import com.imnoob.transport.netty.model.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements CommonSerializer {

    //Kryo线程不安全  需要使用道 threadlocal

    ThreadLocal<Kryo> threadLocal = new ThreadLocal<>();
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    public KryoSerializer() {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        threadLocal.set(kryo);

    }

    @Override
    public byte[] serializer(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = threadLocal.get();
            kryo.writeObject(output, obj);
            threadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            logger.error("序列化时错误发生:", e);
            throw new CommonException(CustomizeException.SERIALIZER_ERROR);
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
       try {
           ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
           Input input = new Input(byteArrayInputStream);
           Kryo kryo = threadLocal.get();
           Object o = kryo.readObject(input, clazz);
           return o;
       }catch (Exception e){
           logger.error("序列化异常");
           throw new CommonException(CustomizeException.SERIALIZER_ERROR);
       }


    }

    @Override
    public int getCode() {
        return CommonSerializer.KRYO_SERIALIZER;
    }
}
