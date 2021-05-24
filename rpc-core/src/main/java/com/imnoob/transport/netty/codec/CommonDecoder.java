package com.imnoob.transport.netty.codec;

import com.imnoob.transport.netty.constant.RpcConstant;
import com.imnoob.transport.netty.model.RpcRequest;
import com.imnoob.transport.netty.model.RpcResponse;
import com.imnoob.transport.netty.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommonDecoder extends ReplayingDecoder<Void> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //TODO 读取 魔数  包类型 序列化类型 数据
        int num = byteBuf.readInt();
        if (num != RpcConstant.MAGIC_NUM){
            logger.error("协议魔数错误");
        }

        int package_type = byteBuf.readInt();
        int serial_type = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getSerializer(serial_type);

        Class<?> clazz ;
        if (package_type == 1){
            clazz = RpcRequest.class;
        }else{
            clazz = RpcResponse.class;
        }

        int len = byteBuf.readInt();
        byte[] data = new byte[len];
        byteBuf.readBytes(data);
        Object obj = serializer.deserialize(data, clazz);
        list.add(obj);

    }
}
